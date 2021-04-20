package smur89.wolt

import java.time.LocalTime

import io.odin.Logger
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.syntax.kleisli._
import org.http4s.{Method, Request, Status}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import smur89.wolt.interpreters.routes
import smur89.wolt.interpreters.routes.OpeningHoursRoute
import smur89.wolt.main._
import smur89.wolt.models.DayOfWeek.{Monday, Tuesday}
import smur89.wolt.models.OpeningHours._
import smur89.wolt.models.Status.{Close, Open}
import smur89.wolt.models.{DayOfWeek, OpeningHours, ValidationError}

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec

class OpeningHoursRoutesTest extends AsyncWordSpec with AsyncIOSpec with Matchers {
  def logger: Logger[IO] = Logger.noop

  "PrettyPrint endpoint" should {
    "Parses the test json" in {
      val prettyPrintRequest =
        Request[IO](Method.POST, uri"/hours/pretty_print")
          .withEntity[OpeningHoursRequest](
            Map[DayOfWeek, List[OpeningHours]]((Monday, List(OpeningHours(Open, LocalTime.now), OpeningHours(Close, LocalTime.now))))
          )

      val response = OpeningHoursRoute(logger).routes.orNotFound(prettyPrintRequest)
      response.asserting(_.status should equal(Status.Ok))
    }
  }

  "Validation" should {
    "Accept only payloads with equal number of Opens to Closes" in {
      val prettyPrintRequest =
        Request[IO](Method.POST, uri"/hours/pretty_print")
          .withEntity[OpeningHoursRequest](
            Map[DayOfWeek, List[OpeningHours]]((Monday, List(OpeningHours(Open, LocalTime.now))))
          )

      routes.OpeningHoursRoute(logger).routes.orNotFound(prettyPrintRequest).assertThrows[ValidationError]
    }

    "Accept only payloads with where Open is always followed by Close" in {
      val prettyPrintRequest =
        Request[IO](Method.POST, uri"/hours/pretty_print")
          .withEntity[OpeningHoursRequest](
            Map[DayOfWeek, List[OpeningHours]]((Monday, List(OpeningHours(Open, LocalTime.now), OpeningHours(Open, LocalTime.now))))
          )

      routes.OpeningHoursRoute(logger).routes.orNotFound(prettyPrintRequest).assertThrows[ValidationError]
    }

    "Accept payloads with where Open is followed by Close across Days" in {
      val prettyPrintRequest =
        Request[IO](Method.POST, uri"/hours/pretty_print")
          .withEntity[OpeningHoursRequest](
            Map[DayOfWeek, List[OpeningHours]](
              (Monday, List(OpeningHours(Open, LocalTime.now))),
              (Tuesday, List(OpeningHours(Close, LocalTime.now)))
            )
          )

      val response = routes.OpeningHoursRoute(logger).routes.orNotFound(prettyPrintRequest)
      response.asserting(_.status should equal(Status.Ok))
    }

    "Accept payloads with where Open is followed by Close across Days, and payload is out of order" in {
      val prettyPrintRequest =
        Request[IO](Method.POST, uri"/hours/pretty_print")
          .withEntity[OpeningHoursRequest](
            Map[DayOfWeek, List[OpeningHours]](
              (Tuesday, List(OpeningHours(Close, LocalTime.now))),
              (Monday, List(OpeningHours(Open, LocalTime.now)))
            )
          )

      val response = routes.OpeningHoursRoute(logger).routes.orNotFound(prettyPrintRequest)
      response.asserting(_.status should equal(Status.Ok))
    }
  }
}
