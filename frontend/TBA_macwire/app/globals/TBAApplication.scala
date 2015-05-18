package globals

import com.softwaremill.macwire._
import gateways.{PlayerGateway, VideoGateway}
import monitoring.MonitoringInterceptor
import play.api.Configuration
import play.api.libs.ws.WSClient
import services.TopVideoService

trait TBAApplication {
  lazy val homepage = wire[controllers.Homepage]
  lazy val players = wire[controllers.Players]

  lazy val topVideoService: TopVideoService = logDuration(wire[TopVideoService])

  lazy val playerGateway: PlayerGateway = logDuration(wire[PlayerGateway])
  lazy val videoGateway: VideoGateway = logDuration(wire[VideoGateway])

  lazy val logDuration = MonitoringInterceptor.logDuration

  // dependencies
  def configuration: Configuration
  def wsClient: WSClient
}

