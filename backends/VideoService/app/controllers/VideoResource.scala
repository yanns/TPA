package controllers

import java.io.{FileInputStream, File}
import models.{VideoRepository, VideoId, PlayerId}
import play.api.{Logger, Play}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import play.api.mvc.{SimpleResult, ResponseHeader, Action, Controller}

object VideoResource extends Controller {

  def videoStream(id: VideoId) = Action { request =>
    Logger.debug(s"streaming video $id")
    VideoRepository.getVideoById(id) match {
      case Some(v) => {
        val file = new File(Play.application.getFile("/public/videos"), s"video-${v.id}.mp4")
        if (file.exists()) {
          request.headers.get(RANGE) match {
            case None => Ok.sendFile(file).as("video/mp4")
            case Some(value) => {
              val (start, end) = value.substring("bytes=".length).split("-") match {
                case x if x.length == 1 => x.head.toLong -> (file.length() - 1)
                case x => x(0).toLong -> x(1).toLong
              }

              val stream = new FileInputStream(file)
              stream.skip(start)

              SimpleResult(
                header = ResponseHeader(PARTIAL_CONTENT,
                  Map(
                    CONNECTION -> "keep-alive",
                    ACCEPT_RANGES -> "bytes",
                    CONTENT_RANGE -> "bytes %d-%d/%d".format(start, end, file.length()),
                    CONTENT_LENGTH -> (end - start + 1).toString,
                    CONTENT_TYPE -> "video/mp4"
                  )
                ),
                body = Enumerator.fromStream(stream)
              )
            }
          }

        } else {
          NotFound
        }
      }
      case None => NotFound
    }
  }

  def topVideos() = Action {
    Ok(Json.toJson(VideoRepository.topVideos()))
  }


  def searchByPlayerId(id: PlayerId) = Action {
    VideoRepository.getVideoForPlayer(id) match {
      case Nil => NotFound
      case videos => Ok(Json.toJson(videos))
    }
  }

}
