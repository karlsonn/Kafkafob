package webapp

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import monocle.Optional
import monocle.macros.GenLens
import net.karlsonn.kafkafob.models
import net.karlsonn.kafkafob.models.{Counter, CounterContainer, Model, topModel}
import slinky.core._
import slinky.core.annotations.react
import slinky.core.facade.Hooks._
import slinky.web.html._
import typings.antDesignIcons.components.AntdIcon
import typings.antDesignIconsSvg.{homeOutlinedMod, mod}
import typings.antd.components.Layout
import typings.antd.components.Layout.Content

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("antd/dist/antd.css", JSImport.Default)
@js.native
object AntdCSS extends js.Any

object ApplicationProxy {
  var update: () => Unit = () => ()
}

@react object KafkafobUIApp {

  private val css = AntdCSS
  case class Props(model: Model)

  val component = FunctionalComponent[Props] { props =>

    val (value, setValue) = useState(0)
    ApplicationProxy.update = () => setValue(value + 1)

    val lens1: Optional[CounterContainer, Counter] = GenLens[CounterContainer](_.counter1).asOptional
    val lens2: Optional[CounterContainer, Counter] = GenLens[CounterContainer](_.counter2).asOptional

    val counterContainer = topModel.container

    val (count, setCount) = useState(0)

    Layout(
      Content(
        div(className := "icon-list")(
          AntdIcon(homeOutlinedMod.default),
          AntdIcon(mod.SyncOutlined)
        ),
        br(),
        button(
          s"count: $count",
          onClick := (_ => setCount(count + 1)),
        ),
        button(
          s"Click to increment counter1 ${lens1.getOption(counterContainer).head.value}",
          onClick := (_ => MessageHandler.actor ! UIMessages.IncCounter(lens1)),
        ),
        button(
          s"Click to increment counter2 ${lens2.getOption(counterContainer).head.value}",
          onClick := (_ => MessageHandler.actor ! UIMessages.IncCounter(lens2))

        )
      )
    )
  }
}

object MessageHandler {
  val system = ActorSystem("default")
  val actor = system.actorOf(Props[ApplicationActor](), name = "ApplicationInfoActor")
  class ApplicationActor extends Actor with ActorLogging {
    def receive = {
      case m: UIMessages.CounterMessage =>
        models.topModel.container = messageUpdate(m)
        ApplicationProxy.update()
    }
    private def messageUpdate: PartialFunction[Any, CounterContainer] = {
      case m: UIMessages.IncCounter =>
        m.lens.modify(c => c.copy(c.value + 1))(models.topModel.container)
    }
  }
}

object UIMessages {
  sealed trait CounterMessage
  case class IncCounter(lens: Optional[CounterContainer, Counter]) extends CounterMessage
}
