package gateways

import models.PlayerId

object PlayerGatewayResponses {

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
