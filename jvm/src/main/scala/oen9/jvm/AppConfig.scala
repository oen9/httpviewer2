package oen9.jvm

import cats.implicits._
import cats.effect.Sync
import cats.implicits._
import ciris._

case class Http(host: String, port: Int)
case class AppConfig(http: Http, assets: String)
object AppConfig:
  val config: ConfigValue[Effect, AppConfig] =
    (
      env("HOST").as[String].default("0.0.0.0"),
      env("PORT").as[Int].default(8080),
      env("assets").or(prop("assets")).or(env("PWD")).as[String]
    ).parMapN { (host, port, assets) =>
      AppConfig(
        http = Http(host = host, port = port),
        assets = assets
      )
    }
