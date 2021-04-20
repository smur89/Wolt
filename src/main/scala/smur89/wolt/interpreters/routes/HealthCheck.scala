package smur89.wolt.interpreters.routes

import smur89.wolt.algebras.routes.HealthCheckAlgebra

import cats.Applicative

object HealthCheck {

  def apply[F[_]: Applicative]: HealthCheckAlgebra[F] = new HealthCheckAlgebra[F] {
    def status: F[Unit] = Applicative[F].unit
  }

}
