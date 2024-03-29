package oen9.jvm.services

import cats.implicits.*
import cats.effect.IO

object IndexMaker:
  def createIndex(paths: Vector[String]): IO[String] = for {
    _     <- paths.pure[IO].ensure(new Exception("denied"))(!_.exists(s => s.isBlank || s == "." || s == ".."))
    index <- generateIndexSite(paths)
  } yield index

  def generateIndexSite(urlPaths: Vector[String]): IO[String] = IO {
    // println(s"paths $urlPaths")
    val src     = urlPaths.foldLeft(os.pwd)(_ / _)
    val files   = os.list(src)
    val srcSize = os.pwd.toString.size

    val prev =
      if (src.toString.substring(srcSize).isBlank) ""
      else
        val backPath = (src / os.up).toString.substring(srcSize)
        s"""<a href="/list$backPath" class="list-group-item list-group-item-action">..</a>"""

    val paths = files.map { path =>
      val p = path.toString.substring(srcSize)
      if (path.toIO.isDirectory)
        s"""<a href="/list$p/" class="list-group-item list-group-item-action"><i class="far fa-folder me-2"></i>$p/</a>"""
      else
        s"""<a href="/list$p" class="list-group-item list-group-item-action"><i class="fas fa-file me-2"></i>$p</a>"""
    }.mkString("\n")

    s"""|
        |<html lang="en" class="h-100">
        |  <head>
        |    <meta charset="UTF-8">
        |    <meta name="viewport" content="width=device-width, initial-scale=1">
        |    <title>httpviewer2</title>
        |    <link href="/assets/bootstrap.min.css" rel="stylesheet">
        |    <script src="https://kit.fontawesome.com/792008ec0a.js"></script>
        |    <link href="front-res/css/style.css" rel="stylesheet">
        |  </head>
        |  <body class="d-flex flex-column h-100">
        |    <div id="main" class="d-flex flex-column h-100">
        |      <div class="list-group">
        |        <a href="/" class="list-group-item list-group-item-action"><i class="fas fa-home me-2"></i>back to home</a>
        |        $prev
        |        $paths
        |      </div>
        |    </div>
        |  </body>
        |</html>
        |""".stripMargin
  }
