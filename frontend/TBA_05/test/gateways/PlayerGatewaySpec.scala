package gateways

import org.specs2.specification.Scope
import play.api.test.PlaySpecification
import components.MockEnvironment

object PlayerGatewaySpec extends PlaySpecification {

  class PlayersGatewayFixture extends MockEnvironment
      with PlayerGatewayComp
      with SimulatedPlayerBackend
      with Scope {

    // the real implementation to test
    override val playerGateway = new PlayerGateway
  }

  "The player gateway" should {
    "parse the json when the user service answer with OK" in new PlayersGatewayFixture {
      val result = await(playerGateway.findPlayer(playerId))
      result must beLike { case FoundPlayer(p) => p.id mustEqual playerId }
    }

    "handle when the player does not exist" in new PlayersGatewayFixture {
      val result = await(playerGateway.findPlayer(unknownPlayerId))
      result must beLike { case PlayerNotFound => ok }
    }
  }

}
