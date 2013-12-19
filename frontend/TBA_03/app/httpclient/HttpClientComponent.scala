package httpclient

import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder

trait HttpClientComponent {

  val httpClient = new HttpClient()

  class HttpClient {
    def url(url: String): WSRequestHolder = WS.url(url)
  }
}
