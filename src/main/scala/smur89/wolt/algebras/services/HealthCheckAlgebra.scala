package smur89.wolt.algebras.services

trait HealthCheckAlgebra[F[_]] {
  def status: F[Unit]
}
