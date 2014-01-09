package httpclient

import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder

trait HttpClientComp {

  trait HttpClient {
    def url(url: String): WSRequestHolder
  }

  def httpClient: HttpClient

}

trait HttpClientCompImpl extends HttpClientComp {

  override val httpClient: HttpClient = new HttpClientImpl

  class HttpClientImpl extends HttpClient {
    def url(url: String): WSRequestHolder = WS.url(url)
  }
}
