package models

import play.api.libs.json.{JsNumber, JsValue, Writes}
import play.api.mvc.PathBindable

case class PlayerId(id: Int) extends AnyVal {
  override def toString = id.toString
}

object PlayerId {
  implicit val json = new Writes[PlayerId] {
    def writes(id: PlayerId): JsValue = JsNumber(id.id)
  }

  implicit def playerIdIdPathBindable(implicit intBinder: PathBindable[Int]) = new PathBindable[PlayerId] {
    def bind(key: String, value: String): Either[String, PlayerId] =
      for (id <- intBinder.bind(key, value).right) yield apply(id)

    def unbind(key: String, playerId: PlayerId): String = intBinder.unbind(key, playerId.id)
  }
}

