package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import services.TopVideoServiceComp

trait Application extends Controller with TopVideoServiceComp {

  def index = Action.async {
    topVideoService.topVideos() map {
      case Some(videos) => Ok(views.html.index(videos))
      case None => Ok(views.html.index(Nil))
    }

  }

}

object Application extends Application