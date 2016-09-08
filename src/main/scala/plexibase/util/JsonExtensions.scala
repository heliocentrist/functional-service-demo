package plexibase.util

import scalaz.stream.Process
import scala.language.higherKinds
import argonaut._
import Argonaut._
import org.joda.time.format.{ISODateTimeFormat}
import org.joda.time.{LocalDateTime}

object JsonExtensions {

  implicit class AddJsonToProcess[M[_], A: EncodeJson](as: Process[M, A]) {
    import Process._

    def asJsonArray: Process[M, String] =
      emit("[") ++ as.map(_.asJson.nospaces).intersperse(",") ++ emit("]")
  }

  def codec[A: EncodeJson: DecodeJson]: CodecJson[A] = CodecJson.derived[A]

  val fmt = ISODateTimeFormat.dateTime()

  //DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")//

  implicit val localDateJson: CodecJson[LocalDateTime] =
    //codec[Long].xmap(new LocalDate(_))(_.toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis)
    codec[String].xmap(LocalDateTime.parse(_, fmt))(_.toString(fmt))
}
