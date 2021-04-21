package smur89.wolt.models

import java.time.LocalTime

import io.circe._
import io.circe.generic.semiauto.deriveCodec

sealed trait WithType[A] {
  val `type`: A
}

final case class OpeningHour(`type`: Status, value: LocalTime) extends WithType[Status]

object OpeningHour {
  type OpeningHourRequest = Map[DayOfWeek, List[OpeningHour]]

  implicit val codec: Codec[OpeningHour] = deriveCodec[OpeningHour]
}
