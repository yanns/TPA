package controllers

import models.{Player, PlayerId}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.WS
import play.api.mvc.{Action, Controller}

object Players extends Controller {

  def details(id: PlayerId) = Action.async {
    WS.url(s"http://localhost:9001/players/$id").get().map { response =>
      val player = response.json.as[Player]
      Ok(views.html.player(player))
    }
  }

}
