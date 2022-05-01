package oen9.jvm.http

import cats.effect.IO
import oen9.jvm.Model.SrvInfo
import sttp.model.Part
import sttp.model.StatusCode
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*

import java.io.File

object AppEndpoints:
  def endpoints = List(check, hostname, uploadFile, getFiles.endpoint)

  val check = endpoint
    .summary("is server alive?")
    .get
    .in("check")
    .out(jsonBody[String])

  val hostname = endpoint
    .summary("get hostname")
    .get
    .in("hostname")
    .out(jsonBody[SrvInfo])

  // http -f POST localhost:8080/file fileField@~/tmp
  val uploadFile = endpoint
    .summary("upload file")
    .post
    .in("file")
    .in(multipartBody[FileForm])
    .out(statusCode(StatusCode.Ok))

  val getFiles = filesGetServerEndpoint[IO]("list")("./")

  case class FileForm(fileField: Option[Part[File]])
