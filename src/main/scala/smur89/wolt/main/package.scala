package smur89.wolt

import cats.Functor
import cats.mtl.Raise
import cats.effect.Bracket

package object main {

  implicit def bracketHandle[F[_]](implicit F: Bracket[F, Throwable]): Raise[F, Throwable] =
    new Raise[F, Throwable] {

      override def raise[E2 <: Throwable, A](e: E2): F[A] = F.raiseError(e)

      override def functor: Functor[F] = F
    }
}
