package componenttests

import com.typesafe.config.ConfigFactory
import gateways.SimulatedPlayerBackend
import globals.TBAApplication
import httpclient.MockWS
import org.scalatest.{Matchers, WordSpec}
import play.api.Configuration
import play.api.test._
import play.api.test.Helpers._

class PlayerDetailSpec extends WordSpec with Matchers {

  class PlayersControllerFixture extends TBAApplication {
    lazy val configuration = Configuration(ConfigFactory.parseString(s"""player.gateway="${SimulatedPlayerBackend.baseURL}""""))
    lazy val wsClient = MockWS(SimulatedPlayerBackend.routes)
  }

  import SimulatedPlayerBackend.{playerId, unknownPlayerId}


  "Details about a player" should {
    "be shown when the backend delivers data" in new PlayersControllerFixture {
      val result = players.details(playerId).apply(FakeRequest())

      status(result) shouldEqual OK
      val html = contentAsString(result)
      html should include ("ze name")
      html should include ("ze height")
      html should include ("ze weight")
      html should include ("ze team")
    }

    "handle when the player does not exist" in new PlayersControllerFixture {
      val result = players.details(unknownPlayerId).apply(FakeRequest())

      status(result) shouldEqual NOT_FOUND
    }
  }

}
