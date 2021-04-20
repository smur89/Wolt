package smur89.wolt.interpreters.services

import smur89.wolt.algebras.services.HealthCheckAlgebra

import cats.Applicative

object HealthCheckService {

  def apply[F[_]: Applicative]: HealthCheckAlgebra[F] = new HealthCheckAlgebra[F] {
    def status: F[Unit] = Applicative[F].unit
  }

}
