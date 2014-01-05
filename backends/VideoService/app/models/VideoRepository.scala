package models


object VideoRepository {

  private val videos = Seq(
    Video(VideoId(1), "awesome powerful dunk", Seq(PlayerId(1))),
    Video(VideoId(2), "best try ever", Seq(PlayerId(2))),
    Video(VideoId(3), "defense beautiful move", Seq(PlayerId(3), PlayerId(2)))
  )

  def topVideos() = videos.slice(0, 3)

  def getVideoById(id: VideoId): Option[Video] = videos.find(_.id == id)

  def getVideoForPlayer(id: PlayerId): Seq[Video] =
    for {
      video <- videos
      if video.players.contains(id)
    } yield video

}
