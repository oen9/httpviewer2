package oen9.js.modules

import org.scalajs.dom.Event
import org.scalajs.dom.File
import org.scalajs.dom.FormData
import org.scalajs.dom.Headers
import org.scalajs.dom.HttpMethod
import org.scalajs.dom.RequestInit
import org.scalajs.dom.RequestMode
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.fetch
import org.scalajs.dom.html
import org.scalajs.dom.raw.XMLHttpRequest
import slinky.core.FunctionalComponent
import slinky.core.SyntheticEvent
import slinky.core.annotations.react
import slinky.core.facade.Fragment
import slinky.core.facade.Hooks._
import slinky.core.facade.React
import slinky.core.facade.ReactElement
import slinky.web.html._

import scala.scalajs.LinkingInfo
import scala.scalajs.js
import scala.util.Failure
import scala.util.Success

@react object Home {
  type Props = Unit

  val component = FunctionalComponent[Props] { _ =>
    val (result, setResult) = useState(None: Option[String])
    val formRef             = React.createRef[html.Form]

    def handleSubmit(e: SyntheticEvent[html.Form, Event]): Unit = {
      e.preventDefault()
      setResult(None)

      val baseUrl = if (LinkingInfo.developmentMode) "http://localhost:8080" else ""

      implicit val ec = scala.scalajs.concurrent.JSExecutionContext.queue
      fetch(
        s"$baseUrl/file",
        new RequestInit {
          method = HttpMethod.POST
          body = new FormData(formRef.current)
          mode = RequestMode.`no-cors`
        }
      ).toFuture.onComplete {
        case Failure(e) =>
          println(s"error: $e")
          e.printStackTrace()
          setResult(Some("error see console for more"))
        case Success(v) =>
          println(s"sccess: $v")
          setResult(Some(s"Uploaded with: ${v.status} ${v.statusText}"))
      }
    }

    div(
      div(a(href := "/list", className := "btn btn-primary w-100", "list server files")),
      div(
        form(
          onSubmit  := (handleSubmit(_)),
          ref       := formRef,
          className := "w-100",
          div(
            className := "mb-1",
            label(htmlFor := "file", className := "form-label"),
            input(
              `type`               := "file",
              className            := "form-control",
              id                   := "fileField",
              name                 := "fileField",
              aria - "describedby" := "fileHelp",
              required
            ),
            div(id        := "fileHelp", className := "form-text", "select file to upload"),
            button(`type` := "submit", className   := "btn btn-primary w-100", "Upload")
          )
        )
      ),
      div(
        className := "row",
        result.fold(div())(div(_))
      )
    )
  }
}
