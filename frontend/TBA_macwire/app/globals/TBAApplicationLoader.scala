package globals

import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.libs.ws.ning.NingWSComponents
import play.api.routing.Router
import router.Routes

class TBAApplicationLoader extends ApplicationLoader {
  override def load(context: Context): Application = {
    Logger.configure(context.environment)
    (new BuiltInComponentsFromContext(context) with TBAComponents).application
  }
}

/**
 * the complete cake for the TBA application
 */
trait TBAComponents
  extends BuiltInComponents // standard play components
  with NingWSComponents // for wsClient
  with TBAApplication {

  import com.softwaremill.macwire._

  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = wire[Routes] withPrefix "/"
}
