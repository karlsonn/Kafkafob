package webapp

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.{Actor, ActorSystem, typed}
import io.grpc.stub.StreamObserver
import io.shad862.kafkafob.admin.proto.describeGroups.DescribeGroupsServiceGrpc.DescribeGroupsService
import io.shad862.kafkafob.admin.proto.describeGroups.{DescribeGroupsReply, DescribeGroupsRequest, DescribeGroupsServiceGrpc, DescribeGroupsServiceGrpcWeb}
import org.scalajs.dom
import slinky.web.ReactDOM
import net.karlsonn.kafkafob.models
import scalapb.grpc.Channels
import typings.react.reactStrings

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Greeter {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])
  def apply():Behavior[Greet] = Behaviors.receive { (context, message) => {
    println(s"Hello ${message.whom}!")
    message.replyTo ! Greeted(message.whom, context.self)
    Behaviors.same
  }}
}

object GreeterBot {
  def apply(max: Int): Behavior[Greeter.Greeted] = {
    bot(0, max)
  }
  def bot(counter: Int, max: Int):Behavior[Greeter.Greeted] = {
    Behaviors.receive { (context, message) =>
      val n = counter + 1
      println(s"Greeting $counter for ${message.whom}")
      if (n == max){
        Behaviors.stopped
      } else {
        message.from ! Greeter.Greet(message.whom, context.self)
        bot(n, max)
      }
    }
  }
}

object GreeterMain {
  final case class SayHello(name: String)
  def apply(): Behavior[SayHello] = {
    Behaviors.setup { context =>
      val greeter = context.spawn(Greeter(), "greeter")
      Behaviors.receiveMessage { message =>
        val replyTo = context.spawn(GreeterBot(3), message.name)
        greeter ! Greeter.Greet(message.name, replyTo)
        Behaviors.same
      }
    }
  }
}


object Main {
  def main(args: Array[String]): Unit = {

    import scala.scalajs.concurrent.JSExecutionContext.Implicits._

    val web = DescribeGroupsServiceGrpcWeb.stub(Channels.grpcwebChannel("http://localhost:8081"))
    web.describeGroupsReplying(new DescribeGroupsRequest(), new StreamObserver[DescribeGroupsReply] {
      override def onNext(value: DescribeGroupsReply): Unit = {
        println(value)
      }
      override def onError(throwable: Throwable): Unit = {
        println(throwable)
      }
      override def onCompleted(): Unit = println("Buy")
    })
    web.describeGroups(new DescribeGroupsRequest()).onComplete(println(_))

    val css = IndexCSS

    ReactDOM.render(KafkafobUIApp(models.topModel), dom.document.getElementById("container"))
    //ReactDOM.render(Samples.component(()), dom.document.getElementById("container"))
  }
}

@JSImport("./index.css", JSImport.Namespace)
@js.native
object IndexCSS extends js.Object
