package webapp

import org.scalajs.dom
import webapp.css.AppCSS
import webapp.routes.AppRouter

import scala.scalajs.js.annotation.JSExport

object UIApp {
  @JSExport
  def main(args: Array[String]): Unit = {
    AppCSS.load
    AppRouter.router().renderIntoDOM(dom.document.getElementById("app"))
  }
}