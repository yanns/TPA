package gateways

import httpclient.MockWS
import models.PlayerId
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Results._

object SimulatedVideoBackend extends Mockito {

  def videoJson(playerId: PlayerId) =
    s"""{
          |  "id": 2,
          |  "summary": "summary of video 2",
          |  "players": [$playerId]
          |}
        """.stripMargin


  def videoRoute(playerId: PlayerId): MockWS.Routes = {
    case ("GET", "http://localhost:9002/videos/top") => Action { Ok(Json.parse(s"[${videoJson(playerId)}]")) }
  }

}
