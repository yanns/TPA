package integrationtests

import java.io.File

import gateways.SimulatedPlayerBackend
import gateways.SimulatedPlayerBackend.{playerId, unknownPlayerId}
import globals.TBAComponents
import httpclient.MockWS
import org.scalatest.{Matchers, WordSpec}
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import play.api.test.{TestServer, WsTestClient}

class PlayerDetailIT extends WordSpec with Matchers {

  val context = ApplicationLoader.createContext(new Environment(new File("."), ApplicationLoader.getClass.getClassLoader, Mode.Test))

  class TBAApplicationLoaderMock extends ApplicationLoader {
    override def load(context: Context): Application = {
      new BuiltInComponentsFromContext(context) with TBAComponents {
        override lazy val wsClient: WSClient = MockWS(SimulatedPlayerBackend.routes)
      }.application
    }
  }

  implicit val application = new TBAApplicationLoaderMock().load(context)
  val server = TestServer(9000, application)

  "Details about a player" should {
    "be shown when the backend delivers data" in {
      running(server) {
        WsTestClient.withClient { ws ⇒
          val response = await(ws.url(s"http://localhost:9000/player/$playerId").get())
          response.status shouldEqual OK
          val html = response.body
          html should include ("ze name")
          html should include ("ze height")
          html should include ("ze weight")
          html should include ("ze team")
        }
      }
    }

    "handle when the player does not exist" in {
      running(server) {
        WsTestClient.withClient { ws ⇒
          val response = await(ws.url(s"http://localhost:9000/player/$unknownPlayerId").get())
          response.status shouldEqual NOT_FOUND
        }
      }

    }
  }
}
