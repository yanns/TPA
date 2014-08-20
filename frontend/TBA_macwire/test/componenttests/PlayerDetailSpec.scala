package componenttests

import com.typesafe.config.ConfigFactory
import gateways.SimulatedPlayerBackend
import gateways.SimulatedPlayerBackend.{playerId, unknownPlayerId}
import globals.TBAApplication
import httpclient.MockWS
import org.specs2.specification.Scope
import play.api.Configuration
import play.api.test.{FakeRequest, PlaySpecification}

class PlayerDetailSpec extends PlaySpecification {

  class PlayersControllerFixture extends TBAApplication with Scope {
    val configuration = Configuration(ConfigFactory.parseString(s"""player.gateway="${SimulatedPlayerBackend.baseURL}""""))
    val ws = MockWS(SimulatedPlayerBackend.routes)
  }


  "Details about a player" should {
    "be shown when the backend delivers data" in new PlayersControllerFixture {
      val result = players.details(playerId).apply(FakeRequest())

      status(result) mustEqual OK
      val html = contentAsString(result)
      html must contain ("ze name")
      html must contain ("ze height")
      html must contain ("ze weight")
      html must contain ("ze team")
    }

    "handle when the player does not exist" in new PlayersControllerFixture {
      val result = players.details(unknownPlayerId).apply(FakeRequest())

      status(result) mustEqual NOT_FOUND
    }
  }

}
