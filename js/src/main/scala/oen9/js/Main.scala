package oen9

import oen9.js.bridges.reactrouter.HashRouter
import oen9.js.modules.MainRouter
import org.scalajs.dom
import org.scalajs.dom.document
import slinky.web.ReactDOM

import scala.scalajs.LinkingInfo

object Main {

  def main(args: Array[String]): Unit = {
    if (LinkingInfo.developmentMode) {
      println("dev mode")
    }
    println("Hello world!")

    val target = document.getElementById("main")
    ReactDOM.render(HashRouter(MainRouter()), target)
  }
}
