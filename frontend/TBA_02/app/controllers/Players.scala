package controllers

import gateways.{FindPlayerResponseError, PlayerNotFound, FoundPlayer, PlayerGateway}
import models.PlayerId
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}

object Players extends Controller {

  val playerGateway = new PlayerGateway

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
