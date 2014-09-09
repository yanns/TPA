package controllers

import gateways.PlayerGateway
import gateways.PlayerGateway.Model.{PlayerNotFound, FoundPlayer}
import models.{PlayerId, Player}
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.mock.MockitoSugar
import org.mockito.BDDMockito._
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class PlayersSpec extends WordSpec with Matchers with MockitoSugar {

  class PlayersControllerFixture {
    val playerGateway= mock[PlayerGateway]

    val players = new Players(playerGateway)

    val playerId = PlayerId(34)
    val player = Player(
      id = playerId,
      name = "ze name",
      height = "ze height",
      weight = "ze weight",
      team = "ze team"
    )
  }

  "The player controller" should {
    "show the player's detail" in new PlayersControllerFixture {
      given (playerGateway.findPlayer(playerId)) willReturn Future.successful(FoundPlayer(player))
      val result = players.details(playerId).apply(FakeRequest())

      status(result) shouldEqual OK
      val html = contentAsString(result)
      html should include (player.name)
      html should include (player.height)
      html should include (player.weight)
      html should include (player.team)
    }

    "handle when the player does not exist" in new PlayersControllerFixture {
      given (playerGateway.findPlayer(playerId)) willReturn Future.successful(PlayerNotFound)
      val result = players.details(playerId).apply(FakeRequest())

      status(result) shouldEqual NOT_FOUND
    }
  }

}
