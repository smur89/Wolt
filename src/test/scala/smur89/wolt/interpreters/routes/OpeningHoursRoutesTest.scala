package smur89.wolt.interpreters.routes

import java.time.LocalTime

import io.circe.parser
import io.odin.Logger
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.syntax.kleisli._
import org.http4s.{Method, Request, Status}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import smur89.wolt.algebras.services.{PrettyPrintAlgebra, ResortAlgebra}
import smur89.wolt.interpreters.routes
import smur89.wolt.interpreters.services.{PrettyPrintService, ResortService}
import smur89.wolt.models.DayOfWeek.{Monday, Tuesday}
import smur89.wolt.models.OpeningHour._
import smur89.wolt.models.Status.{Close, Open}
import smur89.wolt.models.instances._
import smur89.wolt.models.{DayOfWeek, OpeningHour}

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec

class OpeningHourRoutesTest extends AsyncWordSpec with AsyncIOSpec with Matchers {
  private val PrettyPrintUri = uri"/hours/pretty_print"

  private val InitMap: Map[DayOfWeek, List[OpeningHour]] = DayOfWeek.values.foldLeft(Map.empty[DayOfWeek, List[OpeningHour]]) {
    case (acc, day) => acc + (day -> List.empty)
  }

  val logger: Logger[IO] = Logger.noop
  val resorter: ResortAlgebra[IO, DayOfWeek, OpeningHour] = ResortService[IO, DayOfWeek, OpeningHour]
  val formatter: PrettyPrintAlgebra[IO, DayOfWeek, OpeningHour] = PrettyPrintService(resorter, InitMap)

  "PrettyPrint endpoint" should {
    "Parses the test json" in {
      val weekToHours = Map[DayOfWeek, List[OpeningHour]](
        (Monday, List(OpeningHour(Open, LocalTime.of(10, 0)), OpeningHour(Close, LocalTime.of(18, 0))))
      )
      val prettyPrintRequest = Request[IO](Method.POST, PrettyPrintUri).withEntity[OpeningHourRequest](weekToHours)

      val response = OpeningHourRoute(logger, formatter).impl.orNotFound(prettyPrintRequest)
      val responseBody = for {
        r <- response
        body <- r.as[String]
      } yield body

      responseBody.asserting(body => {
        body shouldBe
          """Monday: 10 AM - 6 PM
            |Tuesday: Closed
            |Wednesday: Closed
            |Thursday: Closed
            |Friday: Closed
            |Saturday: Closed
            |Sunday: Closed""".stripMargin
      })
    }

    "Prettify example case" in {
      val json: String = scala.io.Source.fromResource("request/sample.json").getLines().mkString
      val prettifyMe = parser.decode[Map[DayOfWeek, List[OpeningHour]]](json).getOrElse(fail("Couldn't parse json to Request"))
      val prettyPrintRequest = Request[IO](Method.POST, PrettyPrintUri).withEntity[OpeningHourRequest](prettifyMe)
      val response = OpeningHourRoute(logger, formatter).impl.orNotFound(prettyPrintRequest)
      val responseBody = for {
        r <- response
        body <- r.as[String]
      } yield body

      responseBody.asserting(body => {
        body shouldBe
          """Monday: Closed
            |Tuesday: 10 AM - 6 PM
            |Wednesday: Closed
            |Thursday: 10 AM - 6 PM
            |Friday: 10 AM - 1 AM
            |Saturday: 10 AM - 1 AM
            |Sunday: 12 PM - 9 PM""".stripMargin
      })
    }
  }

  "Validation" should {
    "Accept only payloads with equal number of Opens to Closes" in {
      val prettyPrintRequest = Request[IO](Method.POST, PrettyPrintUri).withEntity[OpeningHourRequest](
        Map[DayOfWeek, List[OpeningHour]]((Monday, List(OpeningHour(Open, LocalTime.now))))
      )

      routes
        .OpeningHourRoute(logger, formatter)
        .impl
        .orNotFound(prettyPrintRequest)
        .asserting(response => response.status should equal(Status.BadRequest))
    }

    "Accept only payloads with where Open is always followed by Close" in {
      val prettyPrintRequest = Request[IO](Method.POST, PrettyPrintUri).withEntity[OpeningHourRequest](
        Map[DayOfWeek, List[OpeningHour]]((Monday, List(OpeningHour(Open, LocalTime.now), OpeningHour(Open, LocalTime.now))))
      )

      routes
        .OpeningHourRoute(logger, formatter)
        .impl
        .orNotFound(prettyPrintRequest)
        .asserting(response => response.status should equal(Status.BadRequest))
    }

    "Accept payloads with where Open is followed by Close across Days" in {
      val prettyPrintRequest = Request[IO](Method.POST, PrettyPrintUri).withEntity[OpeningHourRequest](
        Map[DayOfWeek, List[OpeningHour]](
          (Monday, List(OpeningHour(Open, LocalTime.now))),
          (Tuesday, List(OpeningHour(Close, LocalTime.now)))
        )
      )

      val response = routes.OpeningHourRoute(logger, formatter).impl.orNotFound(prettyPrintRequest)
      response.asserting(_.status should equal(Status.Ok))
    }

    "Accept payloads with where Open is followed by Close across Days, and payload is out of order" in {
      val prettyPrintRequest = Request[IO](Method.POST, PrettyPrintUri).withEntity[OpeningHourRequest](
        Map[DayOfWeek, List[OpeningHour]](
          (Tuesday, List(OpeningHour(Close, LocalTime.now))),
          (Monday, List(OpeningHour(Open, LocalTime.now)))
        )
      )

      val response = routes.OpeningHourRoute(logger, formatter).impl.orNotFound(prettyPrintRequest)
      response.asserting(_.status should equal(Status.Ok))
    }
  }
}
