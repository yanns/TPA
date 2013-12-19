package componenttests

import components.RuntimeEnvironment
import controllers.Players
import gateways.SimulatedPlayerBackend
import org.specs2.specification.Scope
import play.api.test.{FakeRequest, PlaySpecification}

object PlayerDetailSpec extends PlaySpecification {

  class PlayersControllerFixture extends RuntimeEnvironment
    with Players
    with SimulatedPlayerBackend
    with Scope


  "Details about a player" should {
    "be shown when the backend delivers data" in new PlayersControllerFixture {
      val result = details(playerId).apply(FakeRequest())

      status(result) mustEqual OK
      val html = contentAsString(result)
      html must contain ("ze name")
      html must contain ("ze height")
      html must contain ("ze weight")
      html must contain ("ze team")
    }

    "handle when the player does not exist" in new PlayersControllerFixture {
      val result = details(unknownPlayerId).apply(FakeRequest())

      status(result) mustEqual NOT_FOUND
    }
  }

}
