package smur89.wolt.models

import java.time.format.DateTimeFormatter

package object instances {
  implicit val renderOpeningHour: Render[OpeningHour] = new Render[OpeningHour] {
    override def render(a: OpeningHour): String = a.value.format(DateTimeFormatter.ofPattern("h:mm a")).replace(":00", "")
  }
}
