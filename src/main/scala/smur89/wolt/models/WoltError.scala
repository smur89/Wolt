package smur89.wolt.models

import cats.Show
import cats.data.NonEmptyList

import scala.util.control.NoStackTrace

sealed abstract class WoltError(val message: String) extends Throwable with NoStackTrace

object WoltError {
  implicit val value: Show[WoltError] = (e: WoltError) => e.message
}

final case class ValidationError(errors: NonEmptyList[String]) extends WoltError(errors.toList.mkString(", "))
