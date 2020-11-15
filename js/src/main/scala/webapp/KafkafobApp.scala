package webapp

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import monocle.Optional
import monocle.macros.GenLens
import net.karlsonn.kafkafob.models
import net.karlsonn.kafkafob.models.{Counter, CounterContainer}
import slinky.core._
import slinky.core.annotations.react
import slinky.web.html._

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("antd/dist/antd.css", JSImport.Default)
@js.native
object AntdCSS extends js.Any

object ApplicationProxy {
  var update: () => Unit = () => ()
}

@react class KafkafobApp extends StatelessComponent {

  type Props = Unit
  private val css = AntdCSS

  val lens1: Optional[CounterContainer, Counter] = GenLens[CounterContainer](_.counter1).asOptional
  val lens2: Optional[CounterContainer, Counter] = GenLens[CounterContainer](_.counter2).asOptional

  override def componentWillMount() = {
    ApplicationProxy.update = () => {
      this.forceUpdate()
    }
  }

  def render() = {
    val model = models.topModel.container
    div(className := "App")(
      header(className := "App-header")(
        h1(className := "App-title")("KafkaFob")
      ),
      button(
        s"Click to increment counter1 ${lens1.getOption(model).head.value}",
        onClick := (_ => {
          MessageHandler.actor ! UIMessages.IncCounter(lens1)
        })
      ),
      br(),
      button(
        s"Click to increment counter2 ${lens2.getOption(model).head.value}",
        onClick := (_ => {
          MessageHandler.actor ! UIMessages.IncCounter(lens2)
        })
      )
    )

  }
}

object MessageHandler {

  val system = ActorSystem("default")
  val actor = system.actorOf(Props[ApplicationActor], name = "ApplicationInfoActor")

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
