package components

import gateways.{PlayerGatewayComponentImpl, VideoGatewayComponentImpl, VideoGatewayComponent, PlayerGatewayComponent}
import httpclient.{HttpClientComponentImpl, HttpClientComponent}
import services.{TopVideoServiceComponentImpl, TopVideoServiceComponent}

trait Registry extends HttpClientComponent
  with PlayerGatewayComponent
  with VideoGatewayComponent
  with TopVideoServiceComponent

trait RuntimeEnvironment extends Registry
  with HttpClientComponentImpl
  with VideoGatewayComponentImpl
  with PlayerGatewayComponentImpl
  with TopVideoServiceComponentImpl