package models

import play.api.libs.json.Json

case class Video(id: VideoId, summary: String, players: Seq[PlayerId])

object Video {
  implicit val json = Json.writes[Video]
}

