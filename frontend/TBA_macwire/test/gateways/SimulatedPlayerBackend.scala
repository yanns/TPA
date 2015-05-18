package gateways

import httpclient.MockWS
import models.PlayerId
import play.api.http.HttpVerbs._
import play.api.libs.json.Json
import play.api.mvc.Action
import play.api.mvc.Results._

object SimulatedPlayerBackend {

  val baseURL = "http://localhost:9001"
  val playerId = PlayerId(34)
  val unknownPlayerId = PlayerId(92)

  val routes: MockWS.Routes = {
    case (GET, u) if u == s"$baseURL/players/$playerId" => Action { Ok(Json.parse(playerJson(playerId))) }
    case _ => Action { NotFound }
  }

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
