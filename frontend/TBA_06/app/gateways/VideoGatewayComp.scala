package gateways

import httpclient.HttpClientComp
import models.{PlayerId, VideoId}
import play.api.http.Status._
import play.api.libs.json.Json
import scala.concurrent.Future

trait VideoGatewayComp {

  def videoGateway: VideoGateway

  sealed trait TopVideosResponse
  case class TopVideos(videos: Seq[Video]) extends TopVideosResponse
  case class TopVideosError(httpStatus: Int) extends TopVideosResponse

  case class Video(id: VideoId, summary: String, players: Seq[PlayerId])

  object Video {
    implicit val json = Json.reads[Video]
  }

  trait VideoGateway {
    def top(): Future[TopVideosResponse]
  }

}

trait VideoGatewayCompImpl extends VideoGatewayComp {

  self: HttpClientComp =>

  override val videoGateway: VideoGateway = new VideoGatewayImpl

  class VideoGatewayImpl extends VideoGateway {

    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val host = "http://localhost:9002"

    def top(): Future[TopVideosResponse] = {
      httpClient.url(s"$host/videos/top").get().map { response =>
        response.status match {
          case OK => TopVideos(response.json.as[Seq[Video]])
          case badStatus => TopVideosError(badStatus)
        }
      }
    }

  }
}