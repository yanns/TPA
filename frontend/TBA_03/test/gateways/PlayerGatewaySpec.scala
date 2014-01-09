package gateways

import httpclient.SimulatedPlayerBackend
import org.specs2.specification.Scope
import play.api.test.PlaySpecification

object PlayerGatewaySpec extends PlaySpecification {

  class PlayersGatewayFixture extends PlayerGatewayComp
    with SimulatedPlayerBackend
    with Scope

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
