package controllers

import gateways.{VideoGatewayCompImpl, PlayerGatewayCompImpl}
import httpclient.HttpClientCompImpl
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import services.{TopVideoServiceCompImpl, TopVideoServiceComp}

trait Application extends Controller with TopVideoServiceComp {

  def index = Action.async {
    topVideoService.topVideos() map {
      case Some(videos) => Ok(views.html.index(videos))
      case None => Ok(views.html.index(Nil))
    }

  }

}

object Application extends Application
  with PlayerGatewayCompImpl
  with VideoGatewayCompImpl
  with HttpClientCompImpl
  with TopVideoServiceCompImpl