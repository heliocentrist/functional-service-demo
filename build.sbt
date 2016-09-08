
name := "functional-service-demo"

version := "1.0"

scalaVersion := "2.11.8"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8", // yes, this is 2 args
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  // "-Ywarn-dead-code", // N.B. doesn't work well with the ??? hole
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture")

scalastyleConfig := baseDirectory.value / "project" / "scalastyle_config.xml"

ivyXML :=
  <dependencies>
    <exclude module="log4j"/>
    <exclude module="commons-logging"/>
  </dependencies>

resolvers ++= Seq(
  Resolver.bintrayRepo("tpolecat", "maven"),
  Resolver.bintrayRepo("oncue", "releases"),
  Resolver.sonatypeRepo("releases")
)

libraryDependencies ++= {
  val http4sVer = "0.14.2a"
  Seq(
    "org.http4s"                 %% "http4s-blaze-server"    % http4sVer,
    "org.http4s"                 %% "http4s-dsl"             % http4sVer,
    "org.http4s"                 %% "http4s-argonaut"        % http4sVer,
    "com.github.alexarchambault" %% "argonaut-shapeless_6.1" % "1.1.1",

    "org.tpolecat"               %% "doobie-core"            % "0.3.0",
    "org.postgresql"              % "postgresql"             % "9.4.1208.jre7",

    "oncue.knobs"                %% "core"                   % "3.8.1a",
    "joda-time"                   % "joda-time"              % "2.9.4",

    "org.joda"                    % "joda-convert"            % "1.8.1",

    "org.slf4j"                   % "log4j-over-slf4j"       % "latest.release",
    "ch.qos.logback"              % "logback-classic"        % "latest.release",
    "org.slf4j"                   % "jcl-over-slf4j"         % "latest.release",
    "org.slf4j"                   % "log4j-over-slf4j"       % "latest.release",
    "org.slf4j"                   % "slf4j-api"              % "latest.release",
    "com.typesafe.scala-logging" %% "scala-logging-slf4j"    % "latest.release",

    "org.tpolecat"               %% "doobie-contrib-h2"      % "0.3.0"          % "test",
    "org.scalatest"              %% "scalatest"              % "2.2.6"          % "test",
    "org.scalacheck"             %% "scalacheck"             % "latest.release" % "test",
    "org.pegdown"                 % "pegdown"                % "latest.release" % "test"
  )
}

parallelExecution in Test := true

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-reports", "-o", "-u", "target/test-reports")