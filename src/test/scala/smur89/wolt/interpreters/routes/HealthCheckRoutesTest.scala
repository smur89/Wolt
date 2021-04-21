package smur89.wolt.interpreters.routes

import io.odin.Logger
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.syntax.kleisli._
import org.http4s.{Method, Request, Status}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec
import smur89.wolt.interpreters.services.HealthCheckService

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec

class HealthCheckRoutesTest extends AsyncWordSpec with AsyncIOSpec with Matchers {
  def logger: Logger[IO] = Logger.noop

  "HealthCheck endpoint" should {
    "return Status 200" in {
      val healthRequest = Request[IO](Method.GET, uri"/healthz")
      val healthCheck = HealthCheckService[IO]

      val response = HealthCheckRoute(logger, healthCheck).impl.orNotFound(healthRequest)
      response.asserting(_.status should equal(Status.NoContent))
    }
  }
}
