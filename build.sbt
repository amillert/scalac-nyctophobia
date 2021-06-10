ThisBuild / version := "0.0.1"
ThisBuild / organization := "amillert"
ThisBuild / scalaVersion := "2.13.5"

def cyan(str: String): String =
  scala.Console.CYAN + str + scala.Console.RESET

def customPrompt(projectName: String): String =
  s"""|
      |[info] Welcome to the ${cyan(projectName)} project!
      |sbt:${cyan(projectName)}> """.stripMargin

shellPrompt := (_ => customPrompt(name.value))

lazy val libs = Seq(
  "dev.zio"       %% "zio"       % "1.0.9",
  "org.scalatest" %% "scalatest" % "3.2.3"
)

lazy val `scalac-nyctophobia` =
  project
    .in(file("."))
    .settings(libraryDependencies ++= libs)
