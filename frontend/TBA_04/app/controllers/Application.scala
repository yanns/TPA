package controllers

import gateways.{VideoGatewayComponentImpl, PlayerGatewayComponentImpl}
import httpclient.HttpClientComponentImpl
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import services.{TopVideoServiceComponentImpl, TopVideoServiceComponent}

trait Application extends Controller with TopVideoServiceComponent {

  def index = Action.async {
    topVideoService.topVideos() map {
      case Some(videos) => Ok(views.html.index(videos))
      case None => Ok(views.html.index(Nil))
    }

  }

}

object Application extends Application
  with PlayerGatewayComponentImpl
  with VideoGatewayComponentImpl
  with HttpClientComponentImpl
  with TopVideoServiceComponentImpl