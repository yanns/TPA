package controllers

import gateways._
import httpclient.HttpClientCompImpl
import models.PlayerId
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.{Action, Controller}
import services.TopVideoServiceCompImpl

trait Players extends Controller with PlayerGatewayComp {

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
  with PlayerGatewayCompImpl
  with VideoGatewayCompImpl
  with HttpClientCompImpl
  with TopVideoServiceCompImpl