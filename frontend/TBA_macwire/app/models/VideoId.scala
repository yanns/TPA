package models


case class VideoId(id: Int) extends AnyVal with IntId

object VideoId extends IntIdBindings[VideoId]

