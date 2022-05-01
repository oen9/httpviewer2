package oen9.jvm

import cats.effect.ExitCode
import cats.effect.IO
import cats.effect.IOApp
import cats.implicits.*
import oen9.jvm.http.AppEndpoints
import oen9.jvm.http.AppRoutes
import oen9.jvm.http.StaticEndpoints
import org.http4s.server.middleware.CORS
import org.typelevel.log4cats.Logger
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.SwaggerUI

object Main extends IOApp, Logging[IO]:
  override def run(args: List[String]): IO[ExitCode] =
    for {
      _   <- log.info("App started")
      cfg <- AppConfig.config.load[IO]
      _   <- app(cfg)
    } yield ExitCode.Success

  def app(cfg: AppConfig): IO[Unit] =
    import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
    import sttp.tapir.openapi.Server
    import sttp.tapir.openapi.circe.yaml.*
    val yaml = OpenAPIDocsInterpreter()
      .toOpenAPI(AppEndpoints.endpoints, "httpviewer2", "0.0.1")
      .servers(
        List(
          Server("http://localhost:8080").description("local server"),
        )
      )
      .toYaml

    val httpApp =
      (
        AppRoutes.routes <+>
          StaticEndpoints.endpoints[IO](cfg.assets) <+>
          Http4sServerInterpreter[IO]().toRoutes(SwaggerUI(yaml = yaml))
      ).orNotFound

    import org.http4s.ember.server.EmberServerBuilder
    import com.comcast.ip4s.port
    import com.comcast.ip4s.Port
    import com.comcast.ip4s.Host
    val cors = CORS.policy.withAllowOriginAll.withAllowCredentials(false)
    import scala.concurrent.duration._
    for {
      _  <- log.trace("Starting http4s ember app")
      ec <- IO.executionContext
      _ <- EmberServerBuilder
        .default[IO]
        .withHostOption(Host.fromString(cfg.http.host))
        .withPort(Port.fromInt(cfg.http.port).getOrElse(port"8080"))
        .withHttpApp(cors(httpApp))
        .build
        .use { s =>
          IO.sleep(30.seconds)
        }
        //.useForever
    } yield ()
