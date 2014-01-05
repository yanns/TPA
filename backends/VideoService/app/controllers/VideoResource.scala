package controllers

import java.io.{FileInputStream, File}
import models.{VideoRepository, VideoId, PlayerId}
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.iteratee.Enumerator
import play.api.libs.json.Json
import play.api.mvc._
import play.api.{Logger, Play}

object VideoResource extends Controller {

  def videoStream(id: VideoId) = Action { request =>
    Logger.debug(s"streaming video $id")

    val streamResult = VideoRepository
      .getVideoById(id)
      .map { v => new File(Play.application.getFile("/public/videos"), s"video-${v.id}.mp4") }
      .filter { f => f.isFile && f.exists() }
      .map { file =>
        request.headers.get(RANGE) match {

          // response without partial content
          case None => Ok.sendFile(file).as("video/mp4").withHeaders(ACCEPT_RANGES -> "bytes")

          case Some(value) if !value.contains("bytes=") => BadRequest("bad 'Range' header")

          // response with partial content
          case Some(value) => {
            val fileLength = file.length()
            val (start, end) = value.substring("bytes=".length).split("-") match {
              case x if x.length == 1 => x.head.toLong -> (fileLength - 1)
              case x => x(0).toLong -> x(1).toLong
            }

            if (start < 0 || end < 0 || start >= fileLength || end >= fileLength) {
              Status(REQUESTED_RANGE_NOT_SATISFIABLE).withHeaders(CONTENT_RANGE -> "bytes */%d".format(fileLength))
            } else {
              val stream = new FileInputStream(file)

              stream.skip(start)

              SimpleResult(
                header = ResponseHeader(PARTIAL_CONTENT,
                  Map(
                    CONNECTION -> "keep-alive",
                    ACCEPT_RANGES -> "bytes",
                    CONTENT_RANGE -> "bytes %d-%d/%d".format(start, end, fileLength),
                    CONTENT_LENGTH -> (end - start + 1).toString,
                    CONTENT_TYPE -> "video/mp4"
                  )
                ),
                body = Enumerator.fromStream(stream)
              )
            }
          }
        }
      }

    streamResult.getOrElse(NotFound)
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
