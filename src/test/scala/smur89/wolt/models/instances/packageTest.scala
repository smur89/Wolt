package smur89.wolt.models.instances

import java.time.LocalTime

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import smur89.wolt.models.OpeningHour
import smur89.wolt.models.Status.Open
import smur89.wolt.models.syntax.Render._

class packageTest extends AnyWordSpec with Matchers {

  "OpeningHour Render instance" should {

    "Format with minutes" in {
      OpeningHour(Open, LocalTime.of(10, 30)).render shouldBe "10:30 AM"
    }

    "Format without minutes for exact hour" in {
      OpeningHour(Open, LocalTime.of(10, 0)).render shouldBe "10 AM"
    }

    "Format without leading 0 for single digit hour" in {
      OpeningHour(Open, LocalTime.of(2, 0)).render shouldBe "2 AM"
    }

  }
}
