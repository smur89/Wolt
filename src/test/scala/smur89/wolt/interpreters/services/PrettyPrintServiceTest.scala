package smur89.wolt.interpreters.services

import java.time.LocalTime

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import smur89.wolt.algebras.services.{PrettyPrintAlgebra, ResortAlgebra}
import smur89.wolt.interpreters.routes.OpeningHourRoute.getYesterday
import smur89.wolt.models.DayOfWeek.Monday
import smur89.wolt.models.Status.{Close, Open}
import smur89.wolt.models.instances._
import smur89.wolt.models.{DayOfWeek, OpeningHour}

import cats.effect.IO

class PrettyPrintServiceTest extends AnyWordSpec with Matchers {
  val noOpResorter: ResortAlgebra[IO, DayOfWeek, OpeningHour] = new ResortAlgebra[IO, DayOfWeek, OpeningHour] {
    override def resort(
        hoursToSort: Map[DayOfWeek, List[OpeningHour]],
        resortedHours: Map[DayOfWeek, List[OpeningHour]],
        alreadyProcessed: List[DayOfWeek]
    )(getPreviousKey: DayOfWeek => DayOfWeek): Map[DayOfWeek, List[OpeningHour]] =
      hoursToSort
  }
  val service: PrettyPrintAlgebra[IO, DayOfWeek, OpeningHour] = PrettyPrintService(noOpResorter, Map.empty)

  "Pretty Printer" should {
    "Prettify single day" in {
      val prettifyMe = Map[DayOfWeek, List[OpeningHour]](
        (Monday, List(OpeningHour(Open, LocalTime.of(10, 0)), OpeningHour(Close, LocalTime.of(18, 0))))
      )
      service.prettify(prettifyMe)(getYesterday) shouldBe "Monday: 10 AM - 6 PM"
    }

  }

}
