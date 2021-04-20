package smur89.wolt.algebras.routes

trait HealthCheckAlgebra[F[_]] {
  def status: F[Unit]
}
