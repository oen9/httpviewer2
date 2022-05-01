package oen9.js.bridges.reactrouter

import slinky.core.ExternalComponent
import slinky.core.annotations.react
import slinky.reactrouter.ReactRouterDOM

import scala.scalajs.js
import scala.scalajs.js.{|, UndefOr}

@react object NavLink extends ExternalComponent {
  case class Props(
    to: String,
    exact: UndefOr[Boolean] = js.undefined,
    activeClassName: UndefOr[String] = js.undefined,
    activeStyle: UndefOr[js.Dynamic] = js.undefined
  )
  override val component = ReactRouterDOM.NavLink
}
