package models

import scala.collection.immutable

object PlayerRepository {

  private val players = immutable.Seq(
    Player(
      id = PlayerId(1),
      name = "James P. Sullivan",
      height = "34 cm",
      weight = "370 g",
      team = "Monstropolis"),

    Player(
      id = PlayerId(2),
      name = "Mike Wazowski",
      height = "12 cm",
      weight = "20 g",
      team = "Monstropolis"),

    Player(
      id = PlayerId(3),
      name= "Totoro",
      height = "38 cm",
      weight = "1 kg",
      team = "Studio Ghibli")
  )

  def playerById(id: PlayerId): Option[Player] = players.find(_.id == id)

  def allPlayers(): Seq[Player] = players

}
