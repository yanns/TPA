package httpclient

import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder

trait HttpClientComp {

  def httpClient: HttpClient

  class HttpClient {
    def url(url: String): WSRequestHolder = WS.url(url)
  }
}

trait HttpClientCompImpl extends HttpClientComp {

  override val httpClient = new HttpClient
}
