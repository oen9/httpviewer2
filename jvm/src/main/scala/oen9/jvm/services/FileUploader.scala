package oen9.jvm.services

import cats.effect.IO
import cats.implicits.*
import oen9.jvm.http.AppEndpoints.FileForm

import java.io.File

object FileUploader:
  def uploadFile(fileForm: FileForm) =
    for {
      _ <- IO.println("got file")
      _ <- fileForm.fileField.map { p =>
        val fileName = p.fileName.fold("unknown-filename")(identity)
        val file     = p.body
        (fileName, file)
      }.fold(IO.raiseError(Exception("no file (maybe unprocessable filename?)")))((saveFile _).tupled)
    } yield ().asRight

  private def saveFile(fileName: String, f: File): IO[Unit] = IO {
    import os._
    val sourceFile = Path(f)
    val destDir    = os.pwd / "uploaded" / java.time.LocalDateTime.now.toString
    val destFile   = destDir / fileName
    os.makeDir.all(destDir)
    os.copy(sourceFile, destFile)
  }
