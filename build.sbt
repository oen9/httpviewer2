val ver = new {
  val dom    = "2.0.0"
  val slinky = "0.7.2"

  val http4s     = "0.23.11"
  val catsEffect = "3.3.11"
  val log4cats   = "2.2.0"
  val logback    = "1.2.5"
  val circe      = "0.14.1"
  val ciris      = "2.3.2"
  val tapir      = "1.0.0-M8"
}

val scala3Version = "3.1.2"
val scala2Version = "2.13.8"

ThisBuild / versionScheme := Some("early-semver")

lazy val sharedSettings = Seq(
  libraryDependencies ++= Seq(),
  name             := "httpviewer2",
  scalaVersion     := scala3Version,
  version          := "0.0.1",
  organization     := "com.github.oen9",
  organizationName := "oen9",
  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-language:higherKinds"
  )
)

lazy val jsSettings = Seq(
  scalaVersion := scala2Version,
  libraryDependencies ++= Seq( // scala3 not supported yet
    "me.shadaj" %%% "slinky-web"          % ver.slinky,
    "me.shadaj" %%% "slinky-react-router" % ver.slinky
  ),
  scalacOptions ++= Seq(
    "-Ymacro-annotations"
  ),
  Compile / npmDependencies ++= Seq(
    "react"            -> "17.0.2",
    "react-dom"        -> "17.0.2",
    "react-popper"     -> "2.2.5",
    "react-router-dom" -> "5.3.0",
    "bootstrap"        -> "5.1.3"
  ),
  scalaJSUseMainModuleInitializer := true,
  webpack / version               := "5.71.0",
  webpackBundlingMode             := BundlingMode.Application,
  fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly()
)

lazy val jvmSettings = Seq(
  libraryDependencies ++= Seq(
    "org.http4s"                  %% "http4s-dsl"               % ver.http4s,
    "org.http4s"                  %% "http4s-ember-server"      % ver.http4s,
    "org.http4s"                  %% "http4s-circe"             % ver.http4s,
    "com.softwaremill.sttp.tapir" %% "tapir-core"               % ver.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"         % ver.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"      % ver.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % ver.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % ver.tapir,
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui"         % ver.tapir,
    "com.lihaoyi"                 %% "os-lib"                   % "0.8.0"
  ),
  libraryDependencies ++= Seq(
    "org.typelevel" %% "log4cats-core"   % ver.log4cats,
    "org.typelevel" %% "log4cats-slf4j"  % ver.log4cats,
    "ch.qos.logback" % "logback-classic" % ver.logback,
    "org.typelevel" %% "cats-effect"     % ver.catsEffect,
    "io.circe"      %% "circe-core"      % ver.circe,
    "io.circe"      %% "circe-generic"   % ver.circe,
    "is.cir"        %% "ciris"           % ver.ciris
  ),
  libraryDependencies ++= Seq(
    "org.scalameta" %% "munit" % "0.7.29" % Test
  )
)

lazy val app =
  crossProject(JSPlatform, JVMPlatform)
    .crossType(CrossType.Full)
    .in(file("."))
    .settings(sharedSettings)
    .jsSettings(jsSettings)
    .jvmSettings(jvmSettings)

lazy val appJS = app.js
  .enablePlugins(ScalaJSBundlerPlugin)

lazy val appJVM = app.jvm
  .enablePlugins(JavaAppPackaging)
  .settings(
    Compile / unmanagedResourceDirectories += (appJS / Compile / resourceDirectory).value,
    Universal / mappings ++= (appJS / Compile / fullOptJS / webpack).value.map { f =>
      f.data -> s"assets/${f.data.getName()}"
    },
    Universal / mappings ++= Seq(
      (appJS / crossTarget).value / "scalajs-bundler" / "main" / "node_modules" / "bootstrap" / "dist" / "css" / "bootstrap.min.css" -> "assets/bootstrap.min.css",
      (appJS / crossTarget).value / "scalajs-bundler" / "main" / "node_modules" / "bootstrap" / "dist" / "css" / "bootstrap.min.css.map" -> "assets/bootstrap.min.css.map",
      (appJS / crossTarget).value / "scalajs-bundler" / "main" / "node_modules" / "bootstrap" / "dist" / "js" / "bootstrap.bundle.min.js" -> "assets/bootstrap.bundle.min.js",
      (appJS / crossTarget).value / "scalajs-bundler" / "main" / "node_modules" / "bootstrap" / "dist" / "js" / "bootstrap.bundle.min.js.map" -> "assets/bootstrap.bundle.min.js.map"
    ),
    bashScriptExtraDefines += """addJava "-Dassets=${app_home}/../assets"""",
    dockerExposedPorts      := Seq(8080),
    dockerBaseImage         := "oen9/sjdk:0.3",
    Docker / dockerUsername := Some("oen9"),
    Docker / daemonUserUid  := None,
    Docker / daemonUser     := "root"
  )
