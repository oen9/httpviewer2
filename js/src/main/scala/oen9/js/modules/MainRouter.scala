package oen9.js.modules

import slinky.core.FunctionalComponent
import slinky.core.ReactComponentClass
import slinky.core.annotations.react
import slinky.reactrouter.Redirect
import slinky.reactrouter.Route
import slinky.reactrouter.Switch

import scalajs.js

@react object MainRouter {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>

    val routerSwitch = Switch(
      Route(exact = true, path = Loc.home, component = Home.component),
      Route(exact = true, path = Loc.about, component = About.component)
    )
    Layout(routerSwitch)
  }

  sealed trait MenuItemType
  case class RegularMenuItem(idx: String, label: String, location: String)              extends MenuItemType
  case class DropDownMenuItems(idx: String, label: String, items: Seq[RegularMenuItem]) extends MenuItemType

  object Loc {
    val home  = "/"
    val about = "/about"
  }
  val menuItems: Seq[MenuItemType] = Seq(
    RegularMenuItem("1", "Home", Loc.home),
    RegularMenuItem("1000", "About", Loc.about)
  )
}
