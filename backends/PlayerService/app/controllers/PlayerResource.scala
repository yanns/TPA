package controllers

import play.api.mvc.{Action, Controller}
import models.{PlayerId, PlayerRepository}
import play.api.libs.json.Json

object PlayerResource extends Controller {

  def all() = Action {
    Ok(Json.toJson(PlayerRepository.allPlayers()))
  }

  def player(id: PlayerId) = Action {
    PlayerRepository.playerById(id) match {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound
    }
  }

  def playerPhoto(id: PlayerId) = controllers.Assets.at("/public/photos", s"player-$id.jpg")

}

