package oen9.jvm.http

import cats.data.Kleisli
import cats.data.OptionT
import cats.effect.IO
import cats.implicits.*
import oen9.jvm.Model.SrvInfo
import oen9.jvm.services.FileUploader
import oen9.jvm.services.IndexMaker
import org.http4s.HttpRoutes
import org.http4s.Request
import sttp.tapir.server.http4s.Http4sServerInterpreter

import java.net.InetAddress

object AppRoutes:
  def routes = Http4sServerInterpreter[IO]().toRoutes(
    List(
      hostname,
      check,
      uploadFile
    )
  ) <+>
    listDirMiddle(Http4sServerInterpreter[IO]().toRoutes(AppEndpoints.getFiles))

  val hostname =
    AppEndpoints.hostname.serverLogic(_ =>
      for {
        hostname <- IO(InetAddress.getLocalHost.getHostName)
      } yield SrvInfo(hostname = hostname).asRight
    )

  val check = AppEndpoints.check.serverLogic(_ => IO.pure("ok".asRight))

  val uploadFile = AppEndpoints.uploadFile.serverLogic(FileUploader.uploadFile)

  private def listDirMiddle(service: HttpRoutes[IO]): HttpRoutes[IO] = Kleisli { (req: Request[IO]) =>
    import org.http4s.Status
    service(req).flatMap {
      case Status.Successful(resp) => OptionT[IO, org.http4s.Response[IO]](IO(resp.some))
      case Status.NotFound(r)      => listDirRoutes(req)
    }
  }

  private val listDirRoutes = {
    import org.http4s.MediaType
    import org.http4s.dsl.io._
    import org.http4s.headers.`Content-Type`
    import org.http4s.implicits._
    HttpRoutes.of[IO] {
      case request @ GET -> Root / "list" =>
        for {
          index <- IndexMaker.generateIndexSite(Vector())
          resp  <- Ok(s"$index")
        } yield resp.withContentType(`Content-Type`(MediaType.text.html))

      case request @ GET -> "list" /: paths =>
        for {
          index <- IndexMaker.createIndex(paths.segments.map(_.toString))
          resp  <- Ok(s"$index")
        } yield resp.withContentType(`Content-Type`(MediaType.text.html))
    }
  }
