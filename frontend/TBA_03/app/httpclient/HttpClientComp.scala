package httpclient

import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder

trait HttpClientComp {

  val httpClient = new HttpClient()

  class HttpClient {
    def url(url: String): WSRequestHolder = WS.url(url)
  }
}
