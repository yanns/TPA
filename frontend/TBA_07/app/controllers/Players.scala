package controllers

import gateways._
import models.PlayerId
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.ws.{WSClient, WS}
import play.api.mvc.{Action, Controller}

class Players(ws: WSClient, app: play.api.Application) extends Controller {

  val playerGateway = new PlayerGateway(ws, app)

  def details(id: PlayerId) = Action.async {
    playerGateway.findPlayer(id) map {
      case FoundPlayer(p) => Ok(views.html.player(p))
      case PlayerNotFound => NotFound(s"player does not exist")
      case FindPlayerResponseError(badStatus) =>
        Logger.error(s"receive HTTP status '$badStatus' for player ID '$id'")
        InternalServerError
    }

  }

}

object Players extends Players(WS.client(play.api.Play.current), play.api.Play.current)