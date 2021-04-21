import sbt._

object Dependencies {
  object versions {
    val cats = "2.5.0"
    val `cats-effect` = "2.4.1"
    val `cats-testing` = "0.5.2"
    val circe = "0.13.0"
    val enumeratumCirce = "1.6.1"
    val http4s = "0.21.22"
    val odin = "0.11.0"
    val scalatest = "3.2.7"
  }

  val cats = Seq("org.typelevel" %% "cats-core" % versions.cats)

  val `cats-effect` = Seq("org.typelevel" %% "cats-effect" % versions.`cats-effect`)

  val circe: Seq[ModuleID] = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % versions.circe)

  val enumeratumCirce = Seq("com.beachape" %% "enumeratum-circe" % versions.enumeratumCirce)

  val http4s: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-blaze-server",
    "org.http4s" %% "http4s-blaze-client",
    "org.http4s" %% "http4s-circe",
    "org.http4s" %% "http4s-dsl"
  ).map(_ % versions.http4s)

  val logger: Seq[ModuleID] =
    Seq("com.github.valskalla" %% "odin-core").map(_ % versions.odin)

  val tests: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest" % versions.scalatest,
    "com.codecommit" %% "cats-effect-testing-scalatest" % versions.`cats-testing`
  ).map(_ % Test)

}
