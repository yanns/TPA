package components

import gateways.{VideoGatewayComp, PlayerGatewayComp}
import httpclient.HttpClientComp
import services.TopVideoServiceComp

trait Registry extends HttpClientComp
  with PlayerGatewayComp
  with VideoGatewayComp
  with TopVideoServiceComp

class RuntimeEnvironment extends Registry {
  override val httpClient = new HttpClient
  override val playerGateway = new PlayerGateway
  override val videoGateway = new VideoGateway
  override val topVideoService = new TopVideoService
}
