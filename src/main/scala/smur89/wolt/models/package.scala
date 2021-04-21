package smur89.wolt

import java.time.{Instant, LocalDate, LocalTime, ZoneOffset}
import java.util.concurrent.TimeUnit

import io.circe.Decoder.Result
import io.circe.syntax._
import io.circe._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

import cats.effect.Sync

package object models {

  implicit val localTimeCodec: Codec[LocalTime] = new Codec[LocalTime] {
    override def apply(a: LocalTime): Json = a.atDate(LocalDate.of(1970, 1, 1)).toInstant(ZoneOffset.UTC).toEpochMilli.asJson

    override def apply(c: HCursor): Result[LocalTime] =
      c.as[Long].map(millis => LocalTime.ofInstant(Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(millis)), ZoneOffset.UTC))
  }

  implicit def entityDecoder[F[_]: Sync, A: Decoder]: EntityDecoder[F, A] = jsonOf[F, A]
  implicit def entityEncoder[F[_]: Sync, A: Encoder]: EntityEncoder[F, A] = jsonEncoderOf[F, A]

}
