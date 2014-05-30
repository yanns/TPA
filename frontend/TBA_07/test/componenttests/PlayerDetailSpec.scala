package componenttests

import controllers.Players
import gateways.SimulatedPlayerBackend
import org.specs2.specification.Scope
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification}

class PlayerDetailSpec extends PlaySpecification {

  class PlayersControllerFixture
    extends Players(SimulatedPlayerBackend.mockWS, FakeApplication())
    with Scope

  "The home page" should {
    "display a list from videos" in new PlayersControllerFixture {
      val result = details(SimulatedPlayerBackend.playerId).apply(FakeRequest())

      status(result) mustEqual OK
      val html = contentAsString(result)
      html must contain ("ze name")
      html must contain ("ze height")
      html must contain ("ze weight")
      html must contain ("ze team")
    }

    "handle when the player does not exist" in new PlayersControllerFixture {
      val result = details(SimulatedPlayerBackend.unknownPlayerId).apply(FakeRequest())

      status(result) mustEqual NOT_FOUND
    }
  }

}
