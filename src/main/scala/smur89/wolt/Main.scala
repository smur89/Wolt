package smur89.wolt

import io.odin.Level
import io.odin.formatter.Formatter
import io.odin.loggers.ConsoleLogger
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import smur89.wolt.interpreters.routes.HealthCheck

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {

  val HttpPort = 3000
  val HttpHost = "0.0.0.0"

  override def run(args: List[String]): IO[ExitCode] = {
    val logger = ConsoleLogger[IO](Formatter.colorful, Level.Trace)
    val statusCheck = HealthCheck[IO]
    val httpApp = Router("/" -> RootRoutes(logger, statusCheck)).orNotFound
    val serverBuilder = BlazeServerBuilder[IO](executionContext).bindHttp(HttpPort, HttpHost).withHttpApp(httpApp)

    logger.info("Starting Http Server...") *> serverBuilder.resource
      .use(_ => logger.info("Http Server Started") *> IO.never)
      .as(ExitCode.Success)
  }
}
