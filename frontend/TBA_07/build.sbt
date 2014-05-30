name := "TBA_07"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies += ws

libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "1.9.5",
  "org.jsoup" % "jsoup" % "1.7.3"
).map(_ % "test")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

