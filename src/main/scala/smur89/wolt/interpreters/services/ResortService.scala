package smur89.wolt.interpreters.services

import smur89.wolt.algebras.services.ResortAlgebra
import smur89.wolt.models.Status.Close
import smur89.wolt.models.{Render, Status, WithType}

import scala.annotation.tailrec

object ResortService {

  def apply[F[_], A: Ordering, B <: WithType[Status]: Render]: ResortAlgebra[F, A, B] =
    new ResortAlgebra[F, A, B] {

      override def resort(
          hoursToSort: Map[A, List[B]],
          resortedHours: Map[A, List[B]],
          alreadyProcessed: List[A]
      )(getPreviousKey: A => A): Map[A, List[B]] = resortImp(hoursToSort, resortedHours, alreadyProcessed)(getPreviousKey)

      @tailrec
      private def resortImp(
          hoursToSort: Map[A, List[B]],
          resortedHours: Map[A, List[B]],
          alreadyProcessed: List[A]
      )(getPreviousKey: A => A): Map[A, List[B]] = hoursToSort.toList.sortBy(_._1) match {
        case (day, closing :: _) :: _ if closing.`type` == Close && alreadyProcessed.contains(day) =>
          resortImp(
            hoursToSort - day,
            resortedHours.updated(day, resortedHours.getOrElse(day, List.empty) :+ closing),
            alreadyProcessed :+ day
          )(getPreviousKey)
        case (day, yesterdaysClose :: todaysHours) :: _ if yesterdaysClose.`type` == Close =>
          val resortedOursNew = if (alreadyProcessed.contains(day)) {
            resortedHours.updated(day, resortedHours.getOrElse(day, List.empty) :+ yesterdaysClose)
          } else resortedHours.updated(day, todaysHours)
          resortImp(hoursToSort - day + (getPreviousKey(day) -> List(yesterdaysClose)), resortedOursNew, alreadyProcessed :+ day)(
            getPreviousKey
          )
        case (day, hours) :: _ =>
          resortImp(hoursToSort - day, resortedHours + (day -> hours), alreadyProcessed :+ day)(getPreviousKey)
        case _ => resortedHours
      }
    }

}
