package components

import gateways.{VideoGatewayComponent, PlayerGatewayComponent}
import httpclient.HttpClientComponent
import services.TopVideoServiceComponent

trait Registry extends HttpClientComponent
  with PlayerGatewayComponent
  with VideoGatewayComponent
  with TopVideoServiceComponent

class RuntimeEnvironment extends Registry {
  override val httpClient = new HttpClient
  override val playerGateway = new PlayerGateway
  override val videoGateway = new VideoGateway
  override val topVideoService = new TopVideoService
}