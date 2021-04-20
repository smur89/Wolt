package smur89.wolt.models

import java.time.LocalTime

import io.circe._
import io.circe.generic.semiauto.deriveCodec

final case class OpeningHours(`type`: Status, value: LocalTime)

object OpeningHours {
  type OpeningHoursRequest = Map[DayOfWeek, List[OpeningHours]]

  implicit val codec: Codec[OpeningHours] = deriveCodec[OpeningHours]

}
