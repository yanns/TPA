package httpclient

import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder

trait HttpClientComponent {

  def httpClient: HttpClient

  class HttpClient {
    def url(url: String): WSRequestHolder = WS.url(url)
  }
}

trait HttpClientComponentImpl extends HttpClientComponent {
  override val httpClient = new HttpClient()
}
