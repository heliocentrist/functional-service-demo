/**
  * Created by yury.liavitski on 21/07/16.
  */
package plexibase

import org.scalatest.{FlatSpec, Matchers}

import scalaz.concurrent.Task

import doobie.imports._

import core.DoobieRepository
import model.Article

class RepositorySpec extends FlatSpec with Matchers with InitDB {

  val tx = DriverManagerTransactor[Task]("org.h2.Driver",
    "jdbc:h2:mem:todo;DB_CLOSE_DELAY=-1",
    "h2username",
    "h2password"
  )
  initDB.run.transact(tx).unsafePerformSync

  val db = new DoobieRepository(tx)

  val testPost = Article(id = 1, name = "myname", content = "mycontent")

  it must "create an article" in {
    val getView = db.create(testPost).unsafePerformSync
    getView.name should be ("myname")
  }

  it must "update an article" in {
    val getView = db.create(testPost).unsafePerformSync
    val updated = db.update(getView.id,
      Article(id = 1, name = "myname2", content = "mycontent2")
    ).unsafePerformSync
    updated.isDefined should be (true)
    updated.get.name should be ("myname2")
    updated.get.content should be ("mycontent2")
  }

  it must "get an article" in {
    val getView = db.create(testPost).unsafePerformSync
    val found = db.get(getView.id).unsafePerformSync
    found.isDefined should be (true)
    found.get.name should be ("myname")
    found.get.content should be ("mycontent")
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
         content text NOT NULL
       );
    """.update

}
