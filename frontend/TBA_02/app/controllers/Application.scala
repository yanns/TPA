package controllers

import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import service.TopVideoService

object Application extends Controller {

  val topVideoService = new TopVideoService

  def index = Action.async {
    topVideoService.topVideos() map {
      case Some(videos) => Ok(views.html.index(videos))
      case None => Ok(views.html.index(Nil))
    }

  }

}