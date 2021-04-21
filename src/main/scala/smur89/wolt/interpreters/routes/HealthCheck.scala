package smur89.wolt.interpreters.routes

import io.odin.Logger
import org.http4s.Method.GET
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io.{->, /}
import org.http4s.{HttpRoutes, Response, Status}
import smur89.wolt.algebras.routes.RoutesAlgebra
import smur89.wolt.algebras.services.HealthCheckAlgebra

import cats.{Applicative, Defer}

import cats.syntax.applicative._
import cats.syntax.apply._

object HealthCheck {

  def apply[F[_]: Applicative: Defer](logger: Logger[F], health: HealthCheckAlgebra[F]): RoutesAlgebra[F] =
    new RoutesAlgebra[F] {
      override def routes: HttpRoutes[F] =
        HttpRoutes.of[F] { case GET -> Root / "healthz" =>
          logger.trace("Checking Health...") *> health.status *> Response[F](Status.Ok).pure[F]
        }
    }

}
