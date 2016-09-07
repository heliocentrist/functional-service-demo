package plexibase.core

import scala.collection.immutable.Vector

import scalaz.std.vector._
import scalaz.syntax.std.vector._
import scalaz.syntax.monad._

import scalaz.concurrent.Task
import scalaz.stream._

import doobie.imports._

import plexibase.model.Article

/**
  * Created by yury.liavitski on 21/07/16.
  */
abstract class Repository {

  def list: Process[Task, Article]

  def create(article: Article): Task[Article]

  def get(id: Long): Task[Option[Article]]

  def update(id: Long, article: Article): Task[Option[Article]]

  def delete(id: Long): Task[Int]
}

class DoobieRepository(tx: Transactor[Task]) extends Repository with DAO {

  def list: Process[Task, Article] =
    getAllArticles.transact(tx)

  def create(article: Article): Task[Article] =
    createArticle(article).transact(tx)

  def get(id: Long): Task[Option[Article]] =
    getArticle(id).transact(tx)

  def update(id: Long, article: Article): Task[Option[Article]] =
    updateArticle(id, article).transact(tx)

  def delete(id: Long): Task[Int] =
    deleteArticle(id).transact(tx)
}

trait DAO {

  def getAllArticles: Process[ConnectionIO, Article] =
    sql"SELECT id, name, content FROM article"
      .query[Article]
      .process

  def deleteArticle(id: Long): ConnectionIO[Int] =
    sql"DELETE FROM article WHERE id = $id".update.run

  def getArticle(id: Long): ConnectionIO[Option[Article]] =
    sql"SELECT id, name, content FROM article WHERE id = $id".query[Article].option

  def createArticle(article: Article): ConnectionIO[Article] =
    for {
      _  <- sql"INSERT INTO article (name, content) VALUES (${article.name}, ${article.content})".update.run
      id <- sql"SELECT lastval()".query[Long].unique
      a  <- sql"SELECT id, name, content FROM article WHERE id = $id".query[Article].unique
    } yield a

  def updateArticle(id: Long, article: Article): ConnectionIO[Option[Article]] =
    for {
      _  <- sql"UPDATE article SET name=${article.name}, content=${article.content} WHERE id=$id".update.run
      a  <- sql"SELECT id, name, content FROM article WHERE id = $id".query[Article].option
    } yield a
}
