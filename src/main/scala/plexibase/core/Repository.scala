package plexibase.core

import scala.collection.immutable.Vector
import scalaz.std.vector._
import scalaz.syntax.std.vector._
import scalaz.syntax.monad._
import scalaz.concurrent.Task
import scalaz.stream._
import doobie.imports._
import java.sql.Timestamp

import org.joda.time.LocalDateTime
import plexibase.model.{Article, ArticlePostView}

/**
  * Created by yury.liavitski on 21/07/16.
  */

class DoobieRepository(tx: Transactor[Task]) extends DAO {

  def list: Process[Task, Article] =
    getAllArticles.transact(tx)

  def create(article: ArticlePostView): Task[Article] =
    createArticle(article).transact(tx)

  def get(id: Long): Task[Option[Article]] =
    getArticle(id).transact(tx)

  def update(id: Long, article: ArticlePostView): Task[Option[Article]] =
    updateArticle(id, article).transact(tx)

  def delete(id: Long): Task[Int] =
    deleteArticle(id).transact(tx)
}

trait DAO {

  implicit val localDateMeta: Meta[LocalDateTime] =
    Meta[Timestamp].xmap(
      s => new LocalDateTime(s.getTime),
      l => new Timestamp(l.toDateTime.getMillis)
    )

  def getAllArticles: Process[ConnectionIO, Article] =
    sql"SELECT id, name, content, created_on FROM article"
      .query[Article]
      .process

  def deleteArticle(id: Long): ConnectionIO[Int] =
    sql"DELETE FROM article WHERE id = $id".update.run

  def getArticle(id: Long): ConnectionIO[Option[Article]] =
    sql"SELECT id, name, content, created_on FROM article WHERE id = $id".query[Article].option

  def createArticle(article: ArticlePostView): ConnectionIO[Article] =
    for {
      _  <- sql"INSERT INTO article (name, content, created_on) VALUES (${article.name}, ${article.content}, ${article.createdOn})".update.run
      id <- sql"SELECT lastval()".query[Long].unique
      a  <- sql"SELECT id, name, content, created_on FROM article WHERE id = $id".query[Article].unique
    } yield a

  def updateArticle(id: Long, article: ArticlePostView): ConnectionIO[Option[Article]] =
    for {
      _  <- sql"UPDATE article SET name=${article.name}, content=${article.content}, created_on=${article.createdOn} WHERE id=$id".update.run
      a  <- sql"SELECT id, name, content, created_on FROM article WHERE id = $id".query[Article].option
    } yield a
}
