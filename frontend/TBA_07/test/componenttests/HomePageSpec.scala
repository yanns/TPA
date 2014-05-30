package componenttests

import controllers.Application
import gateways.{SimulatedVideoBackend, SimulatedPlayerBackend}
import httpclient.MockWS
import org.jsoup.Jsoup
import org.specs2.specification.Scope
import play.api.test.FakeApplication
import play.api.test.{FakeRequest, PlaySpecification}

class HomePageSpec extends PlaySpecification {

  class ApplicationControllerFixture(routes: MockWS.Routes)
    extends Application(MockWS(routes), FakeApplication())
    with Scope {

    val result = index.apply(FakeRequest())

    val contentString = contentAsString(result)
    val content = Jsoup.parse(contentString)
  }


  "The top videos" should {

    "be shown for a known player" in new ApplicationControllerFixture(
        SimulatedVideoBackend.videoRoute(SimulatedPlayerBackend.playerId) orElse
        SimulatedPlayerBackend.playerRoute
      ) {

        status(result) mustEqual OK

        // video is shown
        val video2 = content.select("div#video-2")
        video2 must haveSize (1)

        // player is shown
        val playerInfo = video2.select("li.player")
        playerInfo must haveSize (1)
      }


    "be shown even for an unknown player" in new ApplicationControllerFixture(
        SimulatedVideoBackend.videoRoute(SimulatedPlayerBackend.unknownPlayerId) orElse
        SimulatedPlayerBackend.playerRoute
      ) {

        status(result) mustEqual OK

        // video is shown
        val video2 = content.select("div#video-2")
        video2 must haveSize (1)

        // the player is not shown
        val playerInfo = video2.select("li.player")
        playerInfo must haveSize (0)
      }

  }

}
