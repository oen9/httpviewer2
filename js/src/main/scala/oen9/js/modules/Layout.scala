package oen9.js.modules

import oen9.js.bridges.reactrouter.NavLink
import slinky.core.FunctionalComponent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.core.facade.ReactElement
import slinky.reactrouter.Link
import slinky.web.html._

@react object Layout {
  case class Props(content: ReactElement)

  def createRegularMenuItem(idx: String, label: String, location: String) =
    li(key := idx, className := "nav-item", NavLink(to = location, exact = true)(className := "nav-link", label))

  def makeHeader(props: Props) =
    header(
      nav(
        className := "navbar navbar-expand-md navbar-dark fixed-top bg-dark",
        div(
          className := "container-fluid",
          a(className := "navbar-brand", href := "#", "httpviewer2"),
          button(
            className          := "navbar-toggler",
            `type`             := "button",
            data - "bs-toggle" := "collapse",
            data - "bs-target" := "#navbarCollapse",
            aria - "controls"  := "navbarCollapse",
            aria - "expanded"  := "false",
            aria - "label"     := "Toggle navigation",
            span(className := "navbar-toggler-icon")
          )
        ),
        div(
          className := "collapse navbar-collapse",
          id        := "navbarCollapse",
          ul(
            className := "navbar-nav me-auto mb-2 mb-md-0",
            MainRouter.menuItems.map(_ match {
              case MainRouter.RegularMenuItem(idx, label, location) => createRegularMenuItem(idx, label, location)
              case MainRouter.DropDownMenuItems(idx, label, items) =>
                li(key := idx, className := "nav-item", "drop-down not supported")
            })
          )
        )
      )
    )

  def makeMain(props: Props) =
    main(
      className := "flex-shrink-0",
      div(className := "container mt-5", props.content)
    )

  def makeFooter() =
    footer(
      className := "footer mt-auto py-3 bg-light",
      div(className := "container text-center", span(className := "text-muted", "© 2022 oen"))
    )

  val component = FunctionalComponent[Props] { props =>
    Fragment(
      makeHeader(props),
      makeMain(props),
      makeFooter()
    )
  }
}
