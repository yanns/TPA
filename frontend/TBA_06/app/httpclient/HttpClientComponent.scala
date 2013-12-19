package httpclient

import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder

trait HttpClientComponent {

  trait HttpClient {
    def url(url: String): WSRequestHolder
  }

  def httpClient: HttpClient

}

trait HttpClientComponentImpl extends HttpClientComponent {

  override val httpClient: HttpClient = new HttpClientImpl

  class HttpClientImpl extends HttpClient {
    def url(url: String): WSRequestHolder = WS.url(url)
  }
}
