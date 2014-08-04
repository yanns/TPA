name := "TBA_macwire"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  ws,
  "com.softwaremill.macwire" %% "macros" % "0.7",
  "com.softwaremill.macwire" %% "runtime" % "0.7"
)

libraryDependencies ++= Seq(
  "org.mockito" % "mockito-core" % "1.9.5",
  "org.jsoup" % "jsoup" % "1.7.3"
).map(_ % "test")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

