package webapp.routes

import webapp.components.items.{Item1Data, Item2Data, ItemsInfo}
import webapp.pages.ItemsPage

import japgolly.scalajs.react.extra.router.RouterConfigDsl
import japgolly.scalajs.react.vdom.VdomElement

sealed abstract class Item(val title: String,
                           val routerPath: String,
                           val render: () => VdomElement)

object Item {

  case object Info extends Item("Clusters", "info", () => ItemsInfo())

  case object Item1 extends Item("Topics", "item1", () => Item1Data())

  case object Item2 extends Item("Groups", "item2", () => Item2Data())

  val menu = Vector(Info, Item1, Item2)

  val routes = RouterConfigDsl[Item].buildRule { dsl =>
    import dsl._
    menu
      .map { i =>
        staticRoute(i.routerPath, i) ~> renderR(
          r => ItemsPage(ItemsPage.Props(i, r)))
      }
      .reduce(_ | _)
  }
}
