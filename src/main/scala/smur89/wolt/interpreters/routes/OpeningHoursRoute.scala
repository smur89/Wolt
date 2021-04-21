package smur89.wolt.interpreters.routes

import io.odin.Logger
import org.http4s.Method.POST
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io.{->, /, Ok}
import org.http4s.{HttpRoutes, Response}
import smur89.wolt.algebras.routes.RoutesAlgebra
import smur89.wolt.models.OpeningHours.OpeningHoursRequest
import smur89.wolt.models.ValidationError
import smur89.wolt.models.validation.validateOpeningHoursRequest

import cats.mtl.Raise
import cats.mtl.syntax.raise._
import cats.effect.Sync

import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.syntax.functor._

object OpeningHoursRoute {

  def apply[F[_]: Sync: Raise[*[_], Throwable]](logger: Logger[F]): RoutesAlgebra[F] = new RoutesAlgebra[F] {
    override def routes: HttpRoutes[F] = {
      HttpRoutes.of[F] { case request @ POST -> Root / "hours" / "pretty_print" =>
        for {
          payload <- request.as[OpeningHoursRequest]
          _ <- logger.info(s"Received $payload")
          validatedPayload <- validateOpeningHoursRequest(payload).fold(nel => ValidationError(nel.toList.mkString(", ")).raise, _.pure[F])
          _ <- logger.info(s"Validated: $validatedPayload")
        } yield Response[F](Ok)
      }
    }
  }

}
