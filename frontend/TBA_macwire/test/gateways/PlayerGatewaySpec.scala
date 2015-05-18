package gateways

import com.typesafe.config.ConfigFactory
import gateways.PlayerGateway.Model.{FoundPlayer, PlayerNotFound}
import httpclient.MockWS
import org.scalatest.{Matchers, WordSpec}
import play.api.Configuration
import play.api.test.Helpers._

class PlayerGatewaySpec extends WordSpec with Matchers {

  class PlayersGatewayFixture {
    val ws = MockWS(SimulatedPlayerBackend.routes)

    val configuration = Configuration(ConfigFactory.parseString(s"""player.gateway="${SimulatedPlayerBackend.baseURL}""""))

    // the real implementation to test
    val playerGateway = new PlayerGateway(ws, configuration)
  }

  import SimulatedPlayerBackend.{playerId, unknownPlayerId}

  "The player gateway" should {
    "parse the json when the user service answer with OK" in new PlayersGatewayFixture {
      val result = await(playerGateway.findPlayer(playerId))
      result should matchPattern { case FoundPlayer(p) if p.id == playerId ⇒ }
    }

    "handle when the player does not exist" in new PlayersGatewayFixture {
      val result = await(playerGateway.findPlayer(unknownPlayerId))
      result shouldBe a [PlayerNotFound.type]
    }
  }

}

