package models


object VideoRepository {

  private val videos = Seq(
    Video(VideoId(1), "dunk", Seq(PlayerId(1))),
    Video(VideoId(2), "pass", Seq(PlayerId(2), PlayerId(3)))
  )

  def topVideos() = videos.slice(0, 2)

  def getVideoById(id: VideoId): Option[Video] = videos.find(_.id == id)

  def getVideoForPlayer(id: PlayerId): Seq[Video] =
    for {
      video <- videos
      if video.players.contains(id)
    } yield video

}
