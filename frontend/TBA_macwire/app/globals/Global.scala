package globals

import com.softwaremill.macwire.Macwire
import play.api.GlobalSettings

object Global extends GlobalSettings with Macwire {

  val instanceLookup = wiredInModule(RuntimeEnvironment)

  override def getControllerInstance[A](controllerClass: Class[A]) =
    instanceLookup.lookupSingleOrThrow(controllerClass)
}
