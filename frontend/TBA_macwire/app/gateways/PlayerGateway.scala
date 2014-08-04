package gateways

import models.{Player, PlayerId}
import play.api.{Configuration, Logger}
import play.api.http.Status._
import play.api.libs.ws.WSClient
import scala.concurrent.Future

object PlayerGateway {

  object Model {

    sealed trait FindPlayerResponse
    case class FoundPlayer(player: Player) extends FindPlayerResponse
    case object PlayerNotFound extends FindPlayerResponse
    case class FindPlayerResponseError(httpStatus: Int) extends FindPlayerResponse

  }
}

class PlayerGateway(ws: WSClient, configuration: Configuration) {

  val playerGatewayUrl = configuration.getString("player.gateway").getOrElse(throw new Exception("'player.gateway' must be defined"))

  import play.api.libs.concurrent.Execution.Implicits.defaultContext
  import PlayerGateway.Model._

  def findPlayer(id: PlayerId): Future[FindPlayerResponse] = {
    val url = s"$playerGatewayUrl/players/$id"
    Logger.trace(s"calling '$url'")
    ws.url(url).get().map { response =>
      response.status match {
        case OK =>
          val player = response.json.as[Player]
          Logger.debug(s"found player '$player' for url '$url'")
          FoundPlayer(player)
        case NOT_FOUND => PlayerNotFound
        case badStatus => FindPlayerResponseError(badStatus)
      }
    }
  }

}

