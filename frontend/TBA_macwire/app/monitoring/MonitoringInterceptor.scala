package monitoring

import com.softwaremill.macwire.aop.{ProxyingInterceptor, Interceptor}
import play.api.Logger

import scala.concurrent.Future

object MonitoringInterceptor {

  val log = Logger("duration")

  def logDuration: Interceptor = ProxyingInterceptor { ctx =>
    import play.api.libs.concurrent.Execution.Implicits.defaultContext

    val start = System.currentTimeMillis()

    def perfLog() = log.debug(s"${System.currentTimeMillis() - start}ms for ${ctx.target.getClass.getName}#${ctx.method.getName}(${ctx.parameters.mkString(", ")})")

    val result = ctx.proceed()
    result match {
      case f: Future[_] => f.onComplete(_ => perfLog())
      case _ => perfLog()
    }
    result
  }

}
