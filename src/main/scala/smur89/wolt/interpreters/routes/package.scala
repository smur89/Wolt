package smur89.wolt.interpreters

import io.odin.Logger
import org.http4s.dsl.io.{BadRequest, InternalServerError}
import org.http4s.{Request, Response}
import smur89.wolt.models.{ValidationError, WoltError}

import cats.mtl.Handle
import cats.effect.Sync

import cats.syntax.applicative._
import cats.syntax.apply._

package object routes {
  def errorHandler[F[_]: Sync: Handle[*[_], Throwable]](request: Request[F], logger: Logger[F]): Throwable => F[Response[F]] = {
    case e: ValidationError =>
      logger.warn(s"Invalid request: $request - Errors: ${e.message}") *> Response[F](BadRequest).withEntity(e.message).pure[F]
    case e: WoltError =>
      logger.warn(s"Domain Error: $request - Errors: ${e.message}") *> Response[F](BadRequest).withEntity(e.message).pure[F]
    case e =>
      logger.error(s"Unexpected Error: $request - Errors: ${e.getMessage}") *> Response[F](InternalServerError)
        .withEntity(e.getMessage)
        .pure[F]
  }
}
