package globals

import com.softwaremill.macwire.MacwireMacros.wire
import gateways.{PlayerGateway, VideoGateway}
import monitoring.MonitoringInterceptor
import play.api.libs.ws.{WS, WSClient}
import play.api.{Application, Configuration, Play}
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
  def ws: WSClient
}

trait WSClientModule {
  lazy val ws = WS.client(application)

  // dependencies
  def application: Application
}

trait PlayModule {
  lazy val application = Play.current
  lazy val configuration = application.configuration
}

object RuntimeEnvironment
  extends TBAApplication
  with WSClientModule
  with PlayModule
