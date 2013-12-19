package controllers

import gateways.PlayerGatewayComponent
import models.{Player, PlayerId}
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.test.{FakeRequest, PlaySpecification}
import scala.concurrent.Future

object PlayersSpec extends PlaySpecification {

  trait MockPlayerService extends PlayerGatewayComponent with Mockito {
    override val playerGateway = mock[PlayerGateway]
  }

  class PlayersControllerFixture extends MockPlayerService
      with Players
      with Mockito
      with Scope {

    // must be defined but is not used
    override val httpClient = mock[HttpClient]

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
      playerGateway.findPlayer(playerId) returns Future.successful(FoundPlayer(player))
      val result = details(playerId).apply(FakeRequest())

      status(result) mustEqual OK
      val html = contentAsString(result)
      html must contain (player.name)
      html must contain (player.height)
      html must contain (player.weight)
      html must contain (player.team)
    }

    "handle when the player does not exist" in new PlayersControllerFixture {
      playerGateway.findPlayer(playerId) returns Future.successful(PlayerNotFound)
      val result = details(playerId).apply(FakeRequest())

      status(result) mustEqual NOT_FOUND
    }
  }

}
