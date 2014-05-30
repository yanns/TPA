package gateways

import httpclient.MockWS
import models.PlayerId
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Results._

object SimulatedPlayerBackend extends Mockito {

  val playerId = PlayerId(34)
  val unknownPlayerId = PlayerId(92)

  val playerRoute: MockWS.Routes = {
    case ("GET", u) if u == s"http://localhost:9001/players/$playerId" => Action { Ok(Json.parse(playerJson(playerId))) }
    case _ => Action { NotFound }
  }

  val mockWS = MockWS(playerRoute)

  def playerJson(playerId: PlayerId) =
    s"""{
         |  "id": $playerId,
         |  "name": "ze name",
         |  "height": "ze height",
         |  "weight": "ze weight",
         |  "team": "ze team"
         |}
       """.stripMargin

}
