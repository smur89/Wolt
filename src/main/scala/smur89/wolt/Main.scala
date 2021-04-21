package smur89.wolt

import io.odin.Level
import io.odin.formatter.Formatter
import io.odin.loggers.ConsoleLogger
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import smur89.wolt.algebras.services.{HealthCheckAlgebra, PrettyPrintAlgebra, ResortAlgebra}
import smur89.wolt.interpreters.routes.{HealthCheckRoute, OpeningHourRoute}
import smur89.wolt.interpreters.services.{HealthCheckService, PrettyPrintService, ResortService}
import smur89.wolt.models.instances._
import smur89.wolt.models.{DayOfWeek, OpeningHour}

import cats.effect.{ExitCode, IO, IOApp}

import cats.syntax.semigroupk._

object Main extends IOApp {
  type F[A] = IO[A]

  val HttpPort = 3000
  val HttpHost = "0.0.0.0"
  val InitMap: Map[DayOfWeek, List[OpeningHour]] = DayOfWeek.values.foldLeft(Map.empty[DayOfWeek, List[OpeningHour]]) { case (acc, day) =>
    acc + (day -> List.empty)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val logger = ConsoleLogger[F](Formatter.colorful, Level.Info)
    val healthCheck: HealthCheckAlgebra[F] = HealthCheckService[F]
    val resorter: ResortAlgebra[F, DayOfWeek, OpeningHour] = ResortService[F, DayOfWeek, OpeningHour]
    val formatter: PrettyPrintAlgebra[F, DayOfWeek, OpeningHour] = PrettyPrintService[F, DayOfWeek, OpeningHour](resorter, InitMap)
    val httpApp = (HealthCheckRoute[F](logger, healthCheck).impl <+> OpeningHourRoute(logger, formatter).impl).orNotFound
    val serverBuilder = BlazeServerBuilder[IO](executionContext).bindHttp(HttpPort, HttpHost).withHttpApp(httpApp)

    logger.info("Starting Http Server...") *> serverBuilder.resource
      .use(_ => logger.info("Http Server Started") *> IO.never)
      .as(ExitCode.Success)
  }
}
