name := "TBA_macwire"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  ws,
  "com.softwaremill.macwire" %% "macros" % "1.0.1",
  "com.softwaremill.macwire" %% "runtime" % "1.0.1"
)

libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "1.9.5",
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.scalatest" %% "scalatest" % "2.2.4"
).map(_ % "test")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := play.routes.compiler.InjectedRoutesGenerator
