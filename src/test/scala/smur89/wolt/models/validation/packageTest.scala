package smur89.wolt.models.validation

import java.time.LocalTime

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import smur89.wolt.models.DayOfWeek.{Monday, Tuesday}
import smur89.wolt.models.Status.{Close, Open}
import smur89.wolt.models.{DayOfWeek, OpeningHour}

class packageTest extends AnyWordSpec with Matchers {

  "Validation" should {
    "Accept only payloads with equal number of Opens to Closes" in {
      validateOpeningHourRequest(
        Map[DayOfWeek, List[OpeningHour]]((Monday, List(OpeningHour(Open, LocalTime.now))))
      ).isValid shouldBe false
    }

    "Accept only payloads with where Open is always followed by Close" in {
      validateOpeningHourRequest(
        Map[DayOfWeek, List[OpeningHour]]((Monday, List(OpeningHour(Open, LocalTime.now), OpeningHour(Open, LocalTime.now))))
      ).isValid shouldBe false
    }

    "Accept payloads with where Open is followed by Close across Days" in {
      validateOpeningHourRequest(
        Map[DayOfWeek, List[OpeningHour]](
          (Monday, List(OpeningHour(Open, LocalTime.now))),
          (Tuesday, List(OpeningHour(Close, LocalTime.now)))
        )
      ).isValid shouldBe true
    }

    "Accept payloads with where Open is followed by Close across Days, and payload is out of order" in {
      validateOpeningHourRequest(
        Map[DayOfWeek, List[OpeningHour]](
          (Tuesday, List(OpeningHour(Close, LocalTime.now))),
          (Monday, List(OpeningHour(Open, LocalTime.now)))
        )
      ).isValid shouldBe true
    }

  }
}
