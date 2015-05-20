package services

import gateways.PlayerGateway.Model.{FoundPlayer, PlayerNotFound}
import gateways.VideoGateway.Model.{TopVideos, Video}
import gateways.{PlayerGateway, VideoGateway}
import models.{Player, PlayerId, VideoId}
import org.mockito.BDDMockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, OptionValues, WordSpec}
import play.api.test.Helpers._

import scala.concurrent.Future
import scala.concurrent.Future.{successful ⇒ success}

class TopVideoServiceSpec extends WordSpec with Matchers with MockitoSugar with OptionValues {

  trait TopVideoScope {
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
      given (videoGateway.top()) willReturn success(TopVideos(Seq(video1, video2)))
      given (playerGateway.findPlayer(playerId2)) willReturn success(FoundPlayer(player2))
      given (playerGateway.findPlayer(playerId5)) willReturn success(FoundPlayer(player5))

      val result = await(topVideoService.topVideos())
      result.value should have size 2
    }

    "be shown even if a player is not recognized" in new TopVideoScope {
      given (videoGateway.top()) willReturn success(TopVideos(Seq(video1, video2)))
      given (playerGateway.findPlayer(playerId2)) willReturn success(FoundPlayer(player2))
      given (playerGateway.findPlayer(playerId5)) willReturn success(PlayerNotFound)

      val result = await(topVideoService.topVideos())
      result.value should have size 2
    }

    "be shown even if player service throws an error" in new TopVideoScope {
      given (videoGateway.top()) willReturn success(TopVideos(Seq(video1, video2)))
      given (playerGateway.findPlayer(playerId2)) willReturn success(FoundPlayer(player2))
      given (playerGateway.findPlayer(playerId5)) willReturn Future.failed(new Exception("simulated error"))

      val result = await(topVideoService.topVideos())
      result.value should have size 2
    }
  }

}
