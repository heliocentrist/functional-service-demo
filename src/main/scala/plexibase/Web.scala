package plexibase

import org.http4s.HttpService
import org.http4s.argonaut._
import org.http4s.dsl._
import org.http4s.headers.`Content-Type`
import org.http4s.MediaType._
import org.http4s.EntityDecoder

import argonaut._
import Argonaut._
import ArgonautShapeless._

import doobie.imports._

import model._
import core._
import util.JsonExtensions._

object Web {

  implicit val articleEncoder = jsonEncoderOf[Article]
  implicit val articleDecoder = jsonOf[Article]

  implicit val articlePostViewDecoder = jsonOf[ArticlePostView]

  def service(db: DoobieRepository): HttpService = HttpService {

    case GET -> Root / "articles" =>
      Ok(db.list.asJsonArray)
        .withContentType(Some(`Content-Type`(`application/json`)))

    case req@GET -> Root / "articles" / LongVar(id) =>
      for {
        article <- db.get(id)
        result <- article.fold(NotFound())(Ok(_))
      } yield result

    case req@POST -> Root / "articles" =>
      req.decode[ArticlePostView](a =>
        Ok(db.create(a))
      )

    case req@PUT -> Root / "articles" / LongVar(id) =>
      req.decode[ArticlePostView](a =>
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
