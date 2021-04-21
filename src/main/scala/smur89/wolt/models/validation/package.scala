package smur89.wolt.models

import smur89.wolt.models.OpeningHour.OpeningHourRequest
import smur89.wolt.models.Status.{Close, Open}

import cats.data.{Validated, ValidatedNel}

import cats.syntax.apply._
import cats.syntax.eq._
import cats.syntax.functor._

package object validation {

  def validateOpeningHourRequest(req: OpeningHourRequest): ValidatedNel[String, OpeningHourRequest] =
    (validateOpenClose(req), validateOpenFollowedByClose(req)).tupled.as(req)

  private def validateOpenClose(req: OpeningHourRequest): ValidatedNel[String, OpeningHourRequest] = {
    val openClosed = req.values.flatten.partition(_.`type` == Open)
    Validated.condNel(
      openClosed._2.size === openClosed._1.size,
      req,
      s"Number of 'Open' hours (${openClosed._1.size}) must equal number of 'Close' hours (${openClosed._2.size})."
    )
  }
  private def validateOpenFollowedByClose(req: OpeningHourRequest): ValidatedNel[String, OpeningHourRequest] = {
    val isEachOpenFollowedByClose =
      req.values.flatten.grouped(2).collect { case head :: tail :: Nil => (head, tail) }.forall { case (head, tail) =>
        List(head, tail).exists(_.`type` == Open) && List(head, tail).exists(_.`type` == Close)
      }
    Validated.condNel(
      isEachOpenFollowedByClose,
      req,
      s"Each Opening must be subsequently Closed before opening another availability window"
    )
  }

}
