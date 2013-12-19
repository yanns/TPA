package controllers

import gateways._
import httpclient.HttpClientComponentImpl
import models.PlayerId
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import services.TopVideoServiceComponentImpl

trait Players extends Controller with PlayerGatewayComponent {

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

object Players extends Players
  with PlayerGatewayComponentImpl
  with VideoGatewayComponentImpl
  with HttpClientComponentImpl
  with TopVideoServiceComponentImpl