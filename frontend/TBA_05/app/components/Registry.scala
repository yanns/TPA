package components

import gateways.{PlayerGatewayCompImpl, VideoGatewayCompImpl, VideoGatewayComp, PlayerGatewayComp}
import httpclient.{HttpClientCompImpl, HttpClientComp}
import services.{TopVideoServiceCompImpl, TopVideoServiceComp}

trait Registry extends HttpClientComp
  with PlayerGatewayComp
  with VideoGatewayComp
  with TopVideoServiceComp

trait RuntimeEnvironment extends Registry
  with HttpClientCompImpl
  with VideoGatewayCompImpl
  with PlayerGatewayCompImpl
  with TopVideoServiceCompImpl