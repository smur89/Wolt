package smur89.wolt

import io.odin.Level
import io.odin.formatter.Formatter
import io.odin.loggers.ConsoleLogger
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import smur89.wolt.interpreters.routes.{HealthCheck, OpeningHoursRoute}
import smur89.wolt.interpreters.services.HealthCheckService
import smur89.wolt.main._

import cats.effect.{ExitCode, IO, IOApp}

import cats.syntax.semigroupk._

object Main extends IOApp {
  type F[A] = IO[A]

  val HttpPort = 3000
  val HttpHost = "0.0.0.0"

  override def run(args: List[String]): IO[ExitCode] = {
    val logger = ConsoleLogger[F](Formatter.colorful, Level.Info)
    val healthCheck = HealthCheckService[F]
    val httpApp = (HealthCheck[F](logger, healthCheck).routes <+> OpeningHoursRoute(logger).routes).orNotFound

    val serverBuilder = BlazeServerBuilder[IO](executionContext).bindHttp(HttpPort, HttpHost).withHttpApp(httpApp)

    logger.info("Starting Http Server...") *> serverBuilder.resource
      .use(_ => logger.info("Http Server Started") *> IO.never)
      .as(ExitCode.Success)
  }
}
