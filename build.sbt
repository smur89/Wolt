import Dependencies._
import sbt.addCompilerPlugin

Global / version := "0.1"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    organization := "smur89",
    name := "wolt",
    scalaVersion := "2.12.10",
    Compile / mainClass := Some("smur89.wolt.Main"),
    libraryDependencies ++=
      cats ++
        `cats-effect` ++
        circe ++
        enumeratumCirce ++
        http4s ++
        logger ++
        tests,
    addCompilerPlugin("org.typelevel" % "kind-projector_2.12.10" % "0.11.3")
  )
