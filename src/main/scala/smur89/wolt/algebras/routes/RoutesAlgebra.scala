package smur89.wolt.algebras.routes

import org.http4s.HttpRoutes

trait RoutesAlgebra[F[_]] {
  def impl: HttpRoutes[F]
}
