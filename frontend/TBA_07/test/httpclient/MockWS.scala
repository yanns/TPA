package httpclient

import org.mockito.BDDMockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.specs2.mock.Mockito
import play.api.Logger
import play.api.http.{ContentTypeOf, Writeable}
import play.api.libs.iteratee.{Iteratee, Enumerator}
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse, WSRequestHolder}
import play.api.mvc.EssentialAction
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

/**
 * Mock [[play.api.libs.ws.WS]]
 * @param withRoutes routes defining the mock calls, like case (GET, "/") => Action { Ok("2") }
 */
case class MockWS(withRoutes: MockWS.Routes) extends WSClient with Mockito {

  override def underlying[T]: T = this.asInstanceOf[T]

  require(withRoutes != null)

  private[this] val routes = (method: String, path: String) =>
    if (withRoutes.isDefinedAt(method, path))
      withRoutes.apply(method, path)
    else
      throw new Exception(s"no routes defined for $method $path")

  def url(url: String): WSRequestHolder = {

    def answer(method: String) = new Answer[Future[WSResponse]] {
      def answer(invocation: InvocationOnMock): Future[WSResponse] = {
        // request body
        val action: EssentialAction = routes.apply(method, url)

        val args = invocation.getArguments
        val futureResult = if (args.length == 3) {
          // ws was called with a body content. Extract this content and send it to the mock backend.
          val (bodyContent, mimeType) = extractBodyContent(args)
          Logger.info(s"calling $method $url with '${new String(bodyContent)}' (mimeType:'$mimeType')")
          val requestBody = Enumerator(bodyContent) andThen Enumerator.eof
          val fakeRequest = FakeRequest(method, url).withHeaders((CONTENT_TYPE, mimeType))
          requestBody |>>> action(fakeRequest)
        } else {
          Logger.info(s"calling $method $url")
          val fakeRequest = FakeRequest(method, url)
          action(fakeRequest).run
        }

        futureResult map { result =>
          val wsResponse = mock[WSResponse]
          given (wsResponse.status) willReturn result.header.status
          given (wsResponse.header(any)) willAnswer mockHeaders(result.header.headers)
          val contentAsBytes: Array[Byte] = Await.result(result.body |>>> Iteratee.consume[Array[Byte]](), 5 seconds)
          val body = new String(contentAsBytes, charset(result.header.headers).getOrElse("utf-8"))
          given (wsResponse.body) willReturn body
          val returnedContentType = result.header.headers.get(CONTENT_TYPE).map(_.split(";").take(1).mkString.trim)
          returnedContentType match {
            case Some("application/json") => given(wsResponse.json) willReturn Json.parse(body)
            case Some("text/xml") => given(wsResponse.xml) willReturn scala.xml.XML.loadString(body)
            case Some("application/xml") => given(wsResponse.xml) willReturn scala.xml.XML.loadString(body)
            case Some("text/html") => throw new Exception(s"[$method $url]: receive html '$body'")
            case Some(t) => throw new Exception(s"[$method $url]: cannot parse content type '$t'")
            case None => {}
          }

          wsResponse
        }
      }
    }

    val ws = mock[WSRequestHolder]
    given (ws.withAuth(any, any, any)) willReturn ws
    given (ws.withFollowRedirects(any)) willReturn ws
    given (ws.withHeaders(any)) willReturn ws
    given (ws.withQueryString(any)) willReturn ws
    given (ws.withRequestTimeout(any)) willReturn ws
    given (ws.withVirtualHost(any)) willReturn ws

    given (ws.get()) will answer(GET)
    given (ws.post(any[AnyRef])(any, any)) will answer(POST)
    given (ws.put(any[AnyRef])(any, any)) will answer(PUT)
    given (ws.delete()) will answer(DELETE)

    ws
  }

  private[this] def extractBodyContent[T](args: Array[Object]) = {
    val bodyObject = args(0).asInstanceOf[T]
    val writeable = args(1).asInstanceOf[Writeable[T]]
    val contentTypeOf = args(2).asInstanceOf[ContentTypeOf[T]]
    val mimeType = contentTypeOf.mimeType.get
    (writeable.transform(bodyObject), mimeType)
  }

  private[this] def mockHeaders(headers: Map[String, String]) = new Answer[Option[String]] {
    def answer(invocation: InvocationOnMock): Option[String] = {
      val args = invocation.getArguments
      val key = args(0).asInstanceOf[String]
      headers.get(key)
    }
  }

  private[this] def charset(headers: Map[String, String]): Option[String] = headers.get(CONTENT_TYPE) match {
    case Some(s) if s.contains("charset=") => Some(s.split("; charset=").drop(1).mkString.trim)
    case _ => None
  }
}


object MockWS {
  type Routes = PartialFunction[(String, String), EssentialAction]
}
