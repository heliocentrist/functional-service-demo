/**
  * Created by yury.liavitski on 21/07/16.
  */
package plexibase

import org.scalatest.{FlatSpec, Matchers}

import scalaz.concurrent.Task
import doobie.imports._
import core.DoobieRepository
import model.ArticlePostView
import org.joda.time.LocalDateTime

class RepositorySpec extends FlatSpec with Matchers with InitDB {

  val tx = DriverManagerTransactor[Task]("org.h2.Driver",
    "jdbc:h2:mem:todo;DB_CLOSE_DELAY=-1",
    "h2username",
    "h2password"
  )
  initDB.run.transact(tx).unsafePerformSync

  val db = new DoobieRepository(tx)

  val testPost = ArticlePostView(name = "myname", content = "mycontent", createdOn = new LocalDateTime(2016, 5, 4, 10, 0, 59))
  val testPost2 = ArticlePostView(name = "myname2", content = "mycontent2", createdOn = new LocalDateTime(2016, 9, 8, 11, 11, 11))

  it must "create an article" in {
    val getView = db.create(testPost).unsafePerformSync
    getView.name should be (testPost.name)
    getView.content should be (testPost.content)
    getView.name should be (testPost.name)
  }

  it must "update an article" in {
    val getView = db.create(testPost).unsafePerformSync
    val updated = db.update(getView.id, testPost2).unsafePerformSync
    updated.isDefined should be (true)
    updated.get.name should be (testPost2.name)
    updated.get.content should be (testPost2.content)
    updated.get.createdOn should be (testPost2.createdOn)
  }

  it must "get an article" in {
    val getView = db.create(testPost).unsafePerformSync
    val found = db.get(getView.id).unsafePerformSync
    found.isDefined should be (true)
    found.get.name should be ("myname")
    found.get.content should be ("mycontent")
  }

  it must "get all articles" in {

    val articlesTask = for {
      _ <- db.create(testPost)
      b <- db.create(testPost2)
      as <- db.list.list
    } yield as

    val articles = articlesTask.unsafePerformSync

    articles(0).name should be (testPost.name)
    articles(0).content should be (testPost.content)
    articles(0).createdOn should be (testPost.createdOn)
    articles(1).name should be (testPost2.name)
    articles(1).content should be (testPost2.content)
    articles(1).createdOn should be (testPost2.createdOn)
  }

  it must "delete an article" in {
    val getView = db.create(testPost).unsafePerformSync
    val result = db.delete(getView.id).unsafePerformSync
    result should be (1)
    val found = db.get(getView.id).unsafePerformSync
    found.isDefined should be (false)
  }
}

trait InitDB {

  def initDB: Update0 =
    sql"""
      CREATE TABLE IF NOT EXISTS article (
         id SERIAL PRIMARY KEY,
         name text NOT NULL,
         content text NOT NULL,
         created_on timestamp NOT NULL
       );
    """.update

}
