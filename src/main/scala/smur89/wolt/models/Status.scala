package smur89.wolt.models

import enumeratum.{CirceEnum, Enum, EnumEntry}

import scala.collection.immutable

sealed trait Status extends EnumEntry with EnumEntry.Lowercase with Product with Serializable

object Status extends Enum[Status] with CirceEnum[Status] {
  case object Open extends Status
  case object Close extends Status

  override def values: immutable.IndexedSeq[Status] = findValues
}
