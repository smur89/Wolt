package smur89.wolt.interpreters.services

import java.time.LocalTime

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import smur89.wolt.algebras.services.ResortAlgebra
import smur89.wolt.interpreters.routes.OpeningHourRoute.getYesterday
import smur89.wolt.models.DayOfWeek.{Monday, Tuesday}
import smur89.wolt.models.Status.{Close, Open}
import smur89.wolt.models.instances._
import smur89.wolt.models.{DayOfWeek, OpeningHour}

import cats.effect.IO

class ResortServiceTest extends AnyWordSpec with Matchers {
  val InitMap: Map[DayOfWeek, List[OpeningHour]] = DayOfWeek.values.foldLeft(Map.empty[DayOfWeek, List[OpeningHour]]) { case (acc, day) =>
    acc + (day -> List.empty)
  }
  val service: ResortAlgebra[IO, DayOfWeek, OpeningHour] = ResortService[IO, DayOfWeek, OpeningHour]

  "Resorter" should {
    "Prettify single day" in {
      val noResortRequired = Map[DayOfWeek, List[OpeningHour]](
        (Monday, List(OpeningHour(Open, LocalTime.of(10, 0)), OpeningHour(Close, LocalTime.of(18, 0))))
      )
      service.resort(noResortRequired, InitMap, List.empty)(getYesterday) shouldBe InitMap ++ noResortRequired
    }

    "Prettify with spillover to next day" in {
      val resortMe = Map[DayOfWeek, List[OpeningHour]](
        (Monday, List(OpeningHour(Open, LocalTime.of(10, 0)))),
        (Tuesday, List(OpeningHour(Close, LocalTime.of(1, 0))))
      )

      service.resort(resortMe, InitMap, List.empty)(getYesterday) shouldBe InitMap ++ Map[DayOfWeek, List[OpeningHour]](
        (Monday, List(OpeningHour(Open, LocalTime.of(10, 0)), OpeningHour(Close, LocalTime.of(1, 0))))
      )
    }

  }

}
