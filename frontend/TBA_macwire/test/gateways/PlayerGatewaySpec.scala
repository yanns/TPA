package gateways

import com.typesafe.config.ConfigFactory
import gateways.PlayerGateway.Model.{PlayerNotFound, FoundPlayer}
import gateways.SimulatedPlayerBackend.{playerId, unknownPlayerId}
import httpclient.MockWS
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import play.api.Configuration
import play.api.test.PlaySpecification

class PlayerGatewaySpec extends PlaySpecification {

  class PlayersGatewayFixture extends Mockito with Scope {
    val ws = MockWS(SimulatedPlayerBackend.routes)

    val configuration = Configuration(ConfigFactory.parseString(s"""player.gateway="${SimulatedPlayerBackend.baseURL}""""))

    // the real implementation to test
    val playerGateway = new PlayerGateway(ws, configuration)
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

