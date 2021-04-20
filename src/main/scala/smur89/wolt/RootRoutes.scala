package smur89.wolt

import io.odin.Logger
import org.http4s.Method.GET
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io.{->, /}
import org.http4s.{HttpRoutes, Response, Status}
import smur89.wolt.algebras.routes.HealthCheckAlgebra

import cats.{Applicative, Defer}

import cats.syntax.applicative._
import cats.syntax.apply._

object RootRoutes {

  def apply[F[_]: Applicative: Defer](logger: Logger[F], health: HealthCheckAlgebra[F]): HttpRoutes[F] =
    HttpRoutes.of[F] { case GET -> Root / "healthz" =>
      logger.info("Checking Health...") *> health.status *> Response[F](Status.Ok).pure[F]
    }

}
