package gateways

import httpclient.HttpClientComp
import models.{Player, PlayerId}
import play.api.Logger
import play.api.http.Status._
import scala.concurrent.Future

trait PlayerGatewayComp extends HttpClientComp {

  val playerGateway = new PlayerGateway


  // API

  sealed trait FindPlayerResponse
  case class FoundPlayer(player: Player) extends FindPlayerResponse
  case object PlayerNotFound extends FindPlayerResponse
  case class FindPlayerResponseError(httpStatus: Int) extends FindPlayerResponse


  class PlayerGateway {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val host = "http://localhost:9001"

    def findPlayer(id: PlayerId): Future[FindPlayerResponse] = {
      val url = s"$host/players/$id"
      Logger.trace(s"calling '$url'")
      httpClient.url(url).get().map { response =>
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
}