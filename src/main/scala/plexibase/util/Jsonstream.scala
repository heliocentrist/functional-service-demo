package plexibase.util

import scalaz.stream.Process
import scala.language.higherKinds

import argonaut._
import Argonaut._

object Jsonstream {

  implicit class AddJsonToProcess[M[_], A: EncodeJson](as: Process[M, A]) {
    import Process._

    def asJsonArray: Process[M, String] =
      emit("[") ++ as.map(_.asJson.nospaces).intersperse(",") ++ emit("]")
  }

}
