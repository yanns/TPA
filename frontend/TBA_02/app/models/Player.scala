package models

import play.api.libs.json.Json

case class Player(id: PlayerId, name: String, height: String, weight: String, team: String)

object Player {
  implicit val playerJson = Json.reads[Player]
}
