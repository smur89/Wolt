package smur89.wolt.algebras.services

trait ResortAlgebra[F[_], A, B] {
  def resort(
      hoursToSort: Map[A, List[B]],
      resortedHours: Map[A, List[B]],
      alreadyProcessed: List[A]
  )(getPreviousKey: A => A): Map[A, List[B]]
}
