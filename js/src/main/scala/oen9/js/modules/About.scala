package oen9.js.modules

import slinky.core.annotations.react
import slinky.core.FunctionalComponent
import slinky.web.html._
import scala.scalajs.js
import slinky.core.facade.Hooks._
import slinky.core.facade.Fragment

@react object About {
  type Props = Unit

  def infoTable = Fragment(
    div(className := "row pt-2 pb-2", div(className := "col", "author"), div(className := "col", "oen")),
    div(
      className := "row pt-2 pb-2 bg-light",
      div(className := "col", "github"),
      div(
        className := "col",
        a(
          target := "_blank",
          href   := "https://github.com/oen9/httpviewer2",
          "https://github.com/oen9/httpviewer2"
        )
      )
    )
  )

  val component = FunctionalComponent[Props] { _ =>
    div(
      className := "card",
      div(
        div(className := "card-header", "About"),
        div(
          className := "card-body",
          infoTable
        )
      )
    )
  }
}
