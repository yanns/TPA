package services

import gateways.{PlayerGatewayComponent, VideoGatewayComponent}
import models.{Player, PlayerId, VideoId}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.test.PlaySpecification
import scala.concurrent.Future

class TopVideoServiceSpec extends PlaySpecification {

  class TopVideoScope extends TopVideoServiceComponentImpl
      with VideoGatewayComponent
      with PlayerGatewayComponent
      with Mockito
      with Scope {

    override val videoGateway = mock[VideoGateway]
    override val playerGateway = mock[PlayerGateway]

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
      result.get must haveSize (2)
    }
  }

}
