package smur89.wolt.models

import java.time.LocalTime

import io.circe.parser
import org.scalatest.Succeeded
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import smur89.wolt.models.DayOfWeek.{Friday, Monday, Saturday, Sunday, Thursday, Tuesday, Wednesday}
import smur89.wolt.models.OpeningHour._
import smur89.wolt.models.Status.{Close, Open}

class OpeningHourCodecTest extends AnyWordSpec with Matchers {
  val json: String = scala.io.Source.fromResource("request/sample.json").getLines().mkString

  "PrettyPrint endpoint" should {
    "Parses the test json and retains list ordering" in {
      parser
        .decode[OpeningHourRequest](json)
        .fold(
          e => fail(e.getMessage),
          openingHours => {
            List(
              openingHours.keys.size shouldBe 7,
              openingHours.getOrElse(Monday, fail("Monday did not contain opening hours")) shouldBe empty,
              openingHours
                .getOrElse(Tuesday, fail("Tuesday did not contain opening hours"))
                should contain inOrder (OpeningHour(Open, LocalTime.of(10, 0)), OpeningHour(Close, LocalTime.of(18, 0))),
              openingHours.getOrElse(Wednesday, fail("Wednesday did not contain opening hours")) shouldBe empty,
              openingHours
                .getOrElse(Thursday, fail("Thursday did not contain opening hours"))
                should contain inOrder (OpeningHour(Open, LocalTime.of(10, 0)), OpeningHour(Close, LocalTime.of(18, 0))),
              openingHours
                .getOrElse(Friday, fail("Friday did not contain opening hours"))
                shouldBe List(OpeningHour(Open, LocalTime.of(10, 0))),
              openingHours
                .getOrElse(Saturday, fail("Saturday did not contain opening hours"))
                should contain inOrder (
                  OpeningHour(Close, LocalTime.of(1, 0)),
                  OpeningHour(Open, LocalTime.of(10, 0))
                ),
              openingHours
                .getOrElse(Sunday, fail("Sunday did not contain opening hours"))
                should contain inOrder (
                  OpeningHour(Close, LocalTime.of(1, 0)),
                  OpeningHour(Open, LocalTime.of(12, 0)),
                  OpeningHour(Close, LocalTime.of(21, 0))
                )
            )
          }
        )
        .forall(_ == Succeeded)
    }
  }
}
