package httpclient

import org.specs2.mock.Mockito

trait MockHttpClient extends HttpClientComp with Mockito {

  val mockWS: MockWS
  override val httpClient = mock[HttpClient]
  httpClient.url(any) answers { url  => mockWS.url(url.toString) }
}
