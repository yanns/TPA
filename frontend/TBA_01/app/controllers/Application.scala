package controllers

import models.{Player, TopVideo, PlayerId, VideoId}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc._
import scala.concurrent.Future


object Application extends Controller {

  case class Video(id: VideoId, summary: String, players: Seq[PlayerId])

  implicit val videoReads = Json.reads[Video]

  def index = Action.async {
    val topVideos: Future[Option[Seq[TopVideo]]] = WS.url("http://localhost:9002/videos/top").get().flatMap { response =>
      val videos = response.json.as[Seq[Video]]
      val playerIds = (for (video <- videos) yield video.players).flatten.toSet
      val futurePlayers = playerIds.map { playerId =>
        WS.url(s"http://localhost:9001/players/$playerId").get().map { playerResponse =>
          playerId -> playerResponse.json.asOpt[Player]
        }
      }
      Future.sequence(futurePlayers) map(_.toMap) map { playerMap =>
        val topVideosWithPlayer = videos.map { v =>
          val players = v.players.map(id => playerMap(id)).flatten
          TopVideo(id = v.id, summary = v.summary, players = players)
        }
        Some(topVideosWithPlayer)
      }

    }
    topVideos map {
      case Some(videos) => Ok(views.html.index(videos))
      case None => Ok(views.html.index(Nil))
    }

  }

}