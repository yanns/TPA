package gateways

import httpclient.MockHttpClient
import httpclient.{StandaloneAction, MockWS}
import models.PlayerId
import play.api.libs.json.Json
import play.api.mvc.Results._

trait SimulatedPlayerBackend extends MockHttpClient {

  val GET = "GET"

  val playerId = PlayerId(34)
  val unknownPlayerId = PlayerId(92)

  override val mockWS = new MockWS(withRoutes = {
    case (GET, u) if u == s"http://localhost:9001/players/$playerId" => StandaloneAction { Ok(Json.parse(playerJson(playerId))) }
    case _ => StandaloneAction { NotFound }
  })

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
