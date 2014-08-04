package gateways

import models.{PlayerId, VideoId}
import play.api.Configuration
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient

import scala.concurrent.Future

object VideoGateway {

  object Model {

    sealed trait TopVideosResponse
    case class TopVideos(videos: Seq[Video]) extends TopVideosResponse
    case class TopVideosError(httpStatus: Int) extends TopVideosResponse
    case class Video(id: VideoId, summary: String, players: Seq[PlayerId])

    object Video {
      implicit val json = Json.reads[Video]
    }

  }

}

class VideoGateway(ws: WSClient, configuration: Configuration) {

  val videoGatewayUrl = configuration.getString("video.gateway").getOrElse(throw new Exception("'video.gateway' must be defined"))

  import gateways.VideoGateway.Model._
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  def top(): Future[TopVideosResponse] = {
    ws.url(s"$videoGatewayUrl/videos/top").get().map { response =>
      response.status match {
        case OK => TopVideos(response.json.as[Seq[Video]])
        case badStatus => TopVideosError(badStatus)
      }
    }
  }
}