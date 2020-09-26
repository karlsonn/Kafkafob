package webapp.css

import scalacss.DevDefaults._
import scalacss.internal.mutable.GlobalRegistry
import webapp.components.{LeftNav, TopNav}
import webapp.pages.{HomePage, ItemsPage}

object AppCSS {

  def load = {
    GlobalRegistry.register(GlobalStyle,
                            TopNav.Style,
                            LeftNav.Style,
                            ItemsPage.Style,
                            HomePage.Style)
    GlobalRegistry.onRegistration(_.addToDocument())
  }
}
