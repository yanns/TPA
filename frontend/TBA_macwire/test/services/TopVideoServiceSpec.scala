package services

import gateways.PlayerGateway.Model.{FoundPlayer, PlayerNotFound}
import gateways.VideoGateway.Model.{TopVideos, Video}
import gateways._
import models.{Player, PlayerId, VideoId}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.test.PlaySpecification

import scala.concurrent.Future

class TopVideoServiceSpec extends PlaySpecification {

  trait TopVideoScope extends Mockito with Scope {
    val videoGateway = mock[VideoGateway]
    val playerGateway = mock[PlayerGateway]

    val topVideoService = new TopVideoService(videoGateway, playerGateway)

    val playerId2 = PlayerId(2)
    val playerId5 = PlayerId(5)
    val video1 = Video(VideoId(4), "summary of video 4", Seq(playerId2))
    val video2 = Video(VideoId(24), "summary of video 24", Seq(playerId2, playerId5))
    def player(playerId: PlayerId) = Player(
      id = playerId,
      name = s"player $playerId",
      height = s"height $playerId",
      weight = s"weight $playerId",
      team = s"team $playerId"
    )
    val player2 = player(playerId2)
    val player5 = player(playerId5)
  }

  "the top videos" should {
    "be shown when video and player service answer as expected" in new TopVideoScope {
      videoGateway.top() returns Future.successful(TopVideos(Seq(video1, video2)))
      playerGateway.findPlayer(playerId2) returns Future.successful(FoundPlayer(player2))
      playerGateway.findPlayer(playerId5) returns Future.successful(FoundPlayer(player5))

      val result = await(topVideoService.topVideos())
      result must beSome
      result.get must haveSize(2)
    }

    "be shown even if a player is not recognized" in new TopVideoScope {
      videoGateway.top() returns Future.successful(TopVideos(Seq(video1, video2)))
      playerGateway.findPlayer(playerId2) returns Future.successful(FoundPlayer(player2))
      playerGateway.findPlayer(playerId5) returns Future.successful(PlayerNotFound)

      val result = await(topVideoService.topVideos())
      result must beSome
      result.get must haveSize(2)
    }

    "be shown even if player service throws an error" in new TopVideoScope {
      videoGateway.top() returns Future.successful(TopVideos(Seq(video1, video2)))
      playerGateway.findPlayer(playerId2) returns Future.successful(FoundPlayer(player2))
      playerGateway.findPlayer(playerId5) returns Future.failed(new Exception())

      val result = await(topVideoService.topVideos())
      result must beSome
      result.get must haveSize(2)
    }
  }

}
