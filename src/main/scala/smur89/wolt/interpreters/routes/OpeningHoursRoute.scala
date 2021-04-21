package smur89.wolt.interpreters.routes

import io.odin.Logger
import org.http4s.Method.POST
import org.http4s.dsl.impl.Root
import org.http4s.dsl.io.{->, /, Ok}
import org.http4s.{HttpRoutes, Response}
import smur89.wolt.algebras.routes.RoutesAlgebra
import smur89.wolt.algebras.services.PrettyPrintAlgebra
import smur89.wolt.models.OpeningHour.OpeningHourRequest
import smur89.wolt.models.validation.validateOpeningHourRequest
import smur89.wolt.models.{DayOfWeek, OpeningHour, ValidationError}

import cats.mtl.Handle
import cats.mtl.syntax.handle._
import cats.mtl.syntax.raise._
import cats.effect.Sync

import cats.syntax.applicative._
import cats.syntax.flatMap._
import cats.syntax.functor._

object OpeningHourRoute {

  def apply[F[_]: Sync: Handle[*[_], Throwable]](
      logger: Logger[F],
      formatter: PrettyPrintAlgebra[F, DayOfWeek, OpeningHour]
  ): RoutesAlgebra[F] =
    new RoutesAlgebra[F] {
      override def impl: HttpRoutes[F] = {
        HttpRoutes.of[F] { case request @ POST -> Root / "hours" / "pretty_print" =>
          (for {
            payload <- request.as[OpeningHourRequest]
            validatedPayload <- validateOpeningHourRequest(payload).fold(ValidationError(_).raise, _.pure[F])
            _ <- logger.trace(s"Received Valid Payload: $payload")
            formattedHours <- formatter.prettify(validatedPayload)(getYesterday).pure[F]
            _ <- logger.info(s"Responding with prettified input: $formattedHours")
          } yield Response[F](Ok).withEntity(formattedHours)).handleWith[Throwable](errorHandler(request, logger))
        }
      }
    }

  private[interpreters] def getYesterday(day: DayOfWeek): DayOfWeek = {
    def getIndex = DayOfWeek.indexOf(day) - 1 match {
      case i if i < 0 => DayOfWeek.values.size - 1
      case i          => i
    }
    DayOfWeek.values(getIndex)
  }
}
