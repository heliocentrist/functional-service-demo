package plexibase

import org.http4s.HttpService
import org.http4s.argonaut._
import org.http4s.dsl._
import org.http4s.headers.`Content-Type`
import org.http4s.MediaType._

import argonaut._
import Argonaut._
import ArgonautShapeless._

import doobie.imports._

import model._
import core._
import util.Jsonstream._

object Web {

  implicit val articleEncoder = jsonEncoderOf[Article]
  implicit val articleDecoder = jsonOf[Article]

  def service(db: Repository): HttpService = HttpService {

    case GET -> Root / "articles" =>
      Ok(db.list.asJsonArray)
        .withContentType(Some(`Content-Type`(`application/json`)))

    case req@GET -> Root / "articles" / LongVar(id) =>
      for {
        article <- db.get(id)
        result <- article.fold(NotFound())(Ok(_))
      } yield result

    case req@POST -> Root / "articles" =>
      req.decode[Article](a =>
        Ok(db.create(a))
      )

    case req@PUT -> Root / "articles" / LongVar(id) =>
      req.decode[Article](a =>
        for {
          article <- db.update(id, a)
          result <- article.fold(NotFound())(Ok(_))
        } yield result
      )

    case req@DELETE -> Root / "articles" / LongVar(id) =>
      for {
        _ <- db.delete(id)
        res <- NoContent()
      } yield res

  }
}
