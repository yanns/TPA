package controllers

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.{WS, WSClient}
import play.api.mvc._
import services.TopVideoService

class Application(ws: WSClient, app: play.api.Application) extends Controller {

  val topVideoService = new TopVideoService(ws, app)

  def index = Action.async {
    topVideoService.topVideos() map {
      case Some(videos) => Ok(views.html.index(videos))
      case None => Ok(views.html.index(Nil))
    }

  }

}

object Application extends Application(WS.client(play.api.Play.current), play.api.Play.current)