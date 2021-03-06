package services

import gateways._
import models.TopVideo
import play.api.Logger
import scala.concurrent.Future
import play.api.libs.ws.WSClient

class TopVideoService(ws: WSClient, app: play.api.Application) {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  val videoGateway = new VideoGateway(ws, app)
  val playerGateway = new PlayerGateway(ws, app)

  def topVideos(): Future[Option[Seq[TopVideo]]] = {

    videoGateway.top() flatMap {
      case TopVideos(videos) => {
        Logger.debug(s"found ${videos.length} videos")
        val playerIds = (for (video <- videos) yield video.players).flatten.toSet
        val futurePlayers = playerIds.map { playerId =>
          playerGateway.findPlayer(playerId) map {
            case FoundPlayer(player) => playerId -> Some(player)
            case _ => playerId -> None
          }
        }
        Future.sequence(futurePlayers) map(_.toMap) map { playerMap =>
          val topVideos = videos.map { v =>
            val players = v.players.map(id => playerMap(id)).flatten
            TopVideo(id = v.id, summary = v.summary, players = players)
          }
          Some(topVideos)
        }
      }

      case TopVideosError(badStatus) =>
        Logger.error(s"receive http status '$badStatus'")
        Future.successful(None)
    }

  }

}