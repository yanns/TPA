package service

import scala.concurrent.Future
import gateways._
import play.api.Logger
import models.TopVideo
import gateways.TopVideos
import gateways.FoundPlayer


class TopVideoService {

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  val videoService = new VideoGateway()

  val playerService = new PlayerGateway()

  def topVideos(): Future[Option[Seq[TopVideo]]] = {

    videoService.top() flatMap {
      case TopVideos(videos) => {
        Logger.debug(s"found ${videos.length} videos")
        val playerIds = (for (video <- videos) yield video.players).flatten.toSet
        val futurePlayers = playerIds.map { playerId =>
          playerService.findPlayer(playerId) map {
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
