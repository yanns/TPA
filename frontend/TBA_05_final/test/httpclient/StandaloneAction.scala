package httpclient

import play.api.mvc._
import play.api.libs.iteratee.Iteratee
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

/**
 * A [[httpclient.StandaloneAction]] is an [[play.api.mvc.Action]] that does not require
 * a running play application to be used.
 * With it, it is possible to write tests that do not start a [[play.api.test.FakeApplication]]
 * so that the tests run faster and in parallel.
 *
 * This class can be replaced by a standard [[play.api.mvc.Action]] with a new version from
 * play that contains the following PL:
 * https://github.com/playframework/playframework/pull/2024
 */
trait StandaloneAction[A] extends Action[A] {

  /**
   * Invokes this action.
   *
   * @param request the incoming HTTP request
   * @return the result to be sent to the client
   */
  def apply(request: Request[A]): Future[SimpleResult]

  override def apply(rh: RequestHeader): Iteratee[Array[Byte], SimpleResult] = parser(rh).mapM {
    case Left(r) => Future.successful(r)
    case Right(a) =>
      val request = Request(rh, a)
      // the original implementation in play require a running play application to set the class loader
      // we spare it in testing context
      apply(request)
  }(executionContext)

}

/**
 * Provides helpers for creating `StandaloneAction` values.
 * Simply a copy from [[play.api.mvc.ActionBuilder]], replacing Action with StandaloneAction
 */
trait StandaloneActionBuilder[R[_]] {
  self =>

  /**
   * Constructs an `StandaloneAction`.
   *
   * For example:
   * {{{
   * val echo = StandaloneAction(parse.anyContent) { request =>
   *   Ok("Got request [" + request + "]")
   * }
   * }}}
   *
   * @tparam A the type of the request body
   * @param bodyParser the `BodyParser` to use to parse the request body
   * @param block the action code
   * @return an action
   */
  final def apply[A](bodyParser: BodyParser[A])(block: R[A] => Result): StandaloneAction[A] = async(bodyParser) { req: R[A] =>
    block(req) match {
      case simple: SimpleResult => Future.successful(simple)
      case async: AsyncResult => async.unflatten
    }
  }

  /**
   * Constructs an `StandaloneAction` with default content.
   *
   * For example:
   * {{{
   * val echo = StandaloneAction { request =>
   *   Ok("Got request [" + request + "]")
   * }
   * }}}
   *
   * @param block the action code
   * @return an action
   */
  final def apply(block: R[AnyContent] => Result): StandaloneAction[AnyContent] = apply(BodyParsers.parse.anyContent)(block)

  /**
   * Constructs an `StandaloneAction` with default content, and no request parameter.
   *
   * For example:
   * {{{
   * val hello = StandaloneAction {
   *   Ok("Hello!")
   * }
   * }}}
   *
   * @param block the action code
   * @return an action
   */
  final def apply(block: => Result): StandaloneAction[AnyContent] = apply(_ => block)

  /**
   * Constructs an `StandaloneAction` that returns a future of a result, with default content, and no request parameter.
   *
   * For example:
   * {{{
   * val hello = StandaloneAction.async {
   *   WS.url("http://www.playframework.com").get().map { r =>
   *     if (r.status == 200) Ok("The website is up") else NotFound("The website is down")
   *   }
   * }
   * }}}
   *
   * @param block the action code
   * @return an action
   */
  final def async(block: => Future[SimpleResult]): StandaloneAction[AnyContent] = async(_ => block)

  /**
   * Constructs an `StandaloneAction` that returns a future of a result, with default content.
   *
   * For example:
   * {{{
   * val hello = StandaloneAction.async { request =>
   *   WS.url(request.getQueryString("url").get).get().map { r =>
   *     if (r.status == 200) Ok("The website is up") else NotFound("The website is down")
   *   }
   * }
   * }}}
   *
   * @param block the action code
   * @return an action
   */
  final def async(block: R[AnyContent] => Future[SimpleResult]): StandaloneAction[AnyContent] = async(BodyParsers.parse.anyContent)(block)

  /**
   * Constructs an `StandaloneAction` that returns a future of a result, with default content.
   *
   * For example:
   * {{{
   * val hello = StandaloneAction.async { request =>
   *   WS.url(request.getQueryString("url").get).get().map { r =>
   *     if (r.status == 200) Ok("The website is up") else NotFound("The website is down")
   *   }
   * }
   * }}}
   *
   * @param block the action code
   * @return an action
   */
  final def async[A](bodyParser: BodyParser[A])(block: R[A] => Future[SimpleResult]): StandaloneAction[A] = composeAction(new StandaloneAction[A] {
    def parser = composeParser(bodyParser)
    def apply(request: Request[A]) = try {
      invokeBlock(request, block)
    } catch {
      // NotImplementedError is not caught by NonFatal, wrap it
      case e: NotImplementedError => throw new RuntimeException(e)
      // LinkageError is similarly harmless in Play Framework, since automatic reloading could easily trigger it
      case e: LinkageError => throw new RuntimeException(e)
    }
    override def executionContext = StandaloneActionBuilder.this.executionContext
  })

  /**
   * Invoke the block.  This is the main method that an ActionBuilder has to implement, at this stage it can wrap it in
   * any other actions, modify the request object or potentially use a different class to represent the request.
   *
   * @param request The request
   * @param block The block of code to invoke
   * @return A future of the result
   */
  protected def invokeBlock[A](request: Request[A], block: R[A] => Future[SimpleResult]): Future[SimpleResult]

  /**
   * Compose the parser.  This allows the action builder to potentially intercept requests before they are parsed.
   *
   * @param bodyParser The body parser to compose
   * @return The composed body parser
   */
  protected def composeParser[A](bodyParser: BodyParser[A]): BodyParser[A] = bodyParser

  /**
   * Compose the action with other actions.  This allows mixing in of various actions together.
   *
   * @param action The action to compose
   * @return The composed action
   */
  protected def composeAction[A](action: StandaloneAction[A]): StandaloneAction[A] = action

  /**
   * Get the execution context to run the request in.  Override this if you want a custom execution context
   *
   * @return The execution context
   */
  protected def executionContext: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

}

/**
 * Helper object to create `StandaloneAction` values.
 */
object StandaloneAction extends StandaloneActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]) = block(request)
}
