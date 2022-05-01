package oen9.jvm.http

import cats.effect.Async
import cats.implicits.*
import io.circe.syntax.*
import oen9.jvm.Model.*
import org.http4s.HttpRoutes
import org.http4s.Request
import org.http4s.StaticFile
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.staticcontent._

import java.net.InetAddress

object StaticEndpoints:
  def endpoints[F[_]: Async](assetsPath: String): HttpRoutes[F] =
    val dsl = Http4sDsl[F]
    import dsl._

    def static(file: String, request: Request[F]) =
      StaticFile.fromResource("/" + file, Some(request)).getOrElseF(NotFound())

    def staticAssets(file: String, request: Request[F]) =
      StaticFile.fromString(s"$assetsPath/$file", Some(request)).getOrElseF(NotFound())

    HttpRoutes.of[F] {
      case request @ GET -> Root =>
        static("index.html", request)

      case request @ GET -> Root / path if List(".js", ".css", ".map", ".html", ".ico").exists(path.endsWith) =>
        static(path, request)

      case request @ GET -> "front-res" /: path =>
        val fullPath = "front-res/" + path
        static(fullPath, request)

      case request @ GET -> "assets" /: path =>
        val fullPath = path.toString
        staticAssets(fullPath, request)
    }
