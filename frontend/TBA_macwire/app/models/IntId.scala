package models

import play.api.libs.json._
import play.api.mvc.PathBindable

trait IntId extends Any {
  def id: Int
  override def toString = id.toString
}

trait IntIdBindings[A <: IntId] {
  def apply(id: Int): A

  implicit val json = new Reads[A] {
    def reads(json: JsValue): JsResult[A] = json.validate[Int].map(id => apply(id))
  }

  implicit def intIdPathBindable(implicit intBinder: PathBindable[Int]) = new PathBindable[A] {
    def bind(key: String, value: String): Either[String, A] =
      for (id <- intBinder.bind(key, value).right) yield apply(id)

    def unbind(key: String, intId: A): String = intBinder.unbind(key, intId.id)
  }

}