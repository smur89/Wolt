package smur89.wolt.interpreters.services

import smur89.wolt.algebras.services.{PrettyPrintAlgebra, ResortAlgebra}
import smur89.wolt.models.syntax.Render._
import smur89.wolt.models.{Render, Status, WithType}

object PrettyPrintService {

  def apply[F[_], A: Ordering, B <: WithType[Status]: Render](
      resorter: ResortAlgebra[F, A, B],
      init: Map[A, List[B]]
  ): PrettyPrintAlgebra[F, A, B] =
    new PrettyPrintAlgebra[F, A, B] {
      override def prettify(validatedPayload: Map[A, List[B]])(getPreviousKey: A => A): String = {
        val resortedHours: Map[A, List[B]] = resorter.resort(validatedPayload, init, List.empty)(getPreviousKey)
        render(resortedHours.toList)
      }
    }

  private def render[A: Ordering, B: Render](resorted: List[(A, List[B])]): String = {
    resorted
      .sortBy(_._1)
      .foldLeft(List.empty[String]) { case (acc, (day, hours)) =>
        val hoursFormatted = hours.grouped(2).collect { case head :: tail :: Nil => s"${head.render} - ${tail.render}" }.toList match {
          case Nil => "Closed"
          case s   => s.mkString("", ", ", "")
        }
        acc :+ s"$day: $hoursFormatted"
      }
      .mkString("", "\n", "")
  }

}
