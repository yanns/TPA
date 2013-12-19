package models


case class PlayerId(id: Int) extends AnyVal with IntId

object PlayerId extends IntIdBindings[PlayerId]

