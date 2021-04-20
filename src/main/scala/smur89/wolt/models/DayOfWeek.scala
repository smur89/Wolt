package smur89.wolt.models

import enumeratum.{CirceKeyEnum, Enum, EnumEntry}

import scala.collection.immutable

sealed trait DayOfWeek extends EnumEntry with EnumEntry.Lowercase with Product with Serializable

object DayOfWeek extends Enum[DayOfWeek] with CirceKeyEnum[DayOfWeek] {
  case object Monday extends DayOfWeek
  case object Tuesday extends DayOfWeek
  case object Wednesday extends DayOfWeek
  case object Thursday extends DayOfWeek
  case object Friday extends DayOfWeek
  case object Saturday extends DayOfWeek
  case object Sunday extends DayOfWeek

  override def values: immutable.IndexedSeq[DayOfWeek] = findValues
}
