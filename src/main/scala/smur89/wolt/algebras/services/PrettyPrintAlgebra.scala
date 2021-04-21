package smur89.wolt.algebras.services

trait PrettyPrintAlgebra[F[_], A, B] {
  def prettify(validatedPayload: Map[A, List[B]])(getYesterday: A => A): String
}
