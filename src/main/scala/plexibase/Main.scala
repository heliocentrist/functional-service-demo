package plexibase

import scalaz.syntax.applicative._
import scalaz.std.list._
import scalaz.concurrent.Task
import knobs.{ClassPathResource, Required}
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware.CORS
import org.http4s.server.{Server, ServerApp}
import doobie.imports.DriverManagerTransactor
import core.DoobieRepository
import Web._

object Main extends ServerApp {

  def server(args: List[String]): Task[Server] =
    for {

      config <- knobs.loadImmutable(Required(ClassPathResource("application.conf")).pure[List])

      dbconf = config.subconfig("db")
      tx = DriverManagerTransactor[Task]("org.postgresql.Driver",
        s"jdbc:postgresql://${dbconf.require[String]("host")}/${dbconf.require[String]("name")}",
        dbconf.require[String]("user"),
        dbconf.require[String]("password")
      )
      db = new DoobieRepository(tx)

      server <- BlazeBuilder.bindHttp(config.require[Int]("web.port"))
        .mountService(CORS(service(db)), config.require[String]("web.path"))
        .start

    } yield server

}
