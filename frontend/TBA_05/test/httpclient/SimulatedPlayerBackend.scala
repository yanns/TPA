package httpclient

import models.PlayerId
import org.specs2.mock.Mockito
import play.api.libs.json.Json
import play.api.mvc.Results._

trait SimulatedPlayerBackend extends HttpClientComp with Mockito {

  val playerId = PlayerId(34)
  val unknownPlayerId = PlayerId(92)

  val mockWS = MockWS {
    case ("GET", u) if u == s"http://localhost:9001/players/$playerId" => StandaloneAction { Ok(Json.parse(playerJson(playerId))) }
    case _ => StandaloneAction { NotFound }
  }

  override val httpClient = mock[HttpClient]
  httpClient.url(any) answers { url  => mockWS.url(url.toString) }

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
