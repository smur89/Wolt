package smur89.wolt.models

import cats.Show

import scala.util.control.NoStackTrace

sealed abstract class WoltError(val message: String) extends Throwable with NoStackTrace

object WoltError {
  implicit val value: Show[WoltError] = (e: WoltError) => e.message
}

final case class ValidationError(override val message: String) extends WoltError(message)
