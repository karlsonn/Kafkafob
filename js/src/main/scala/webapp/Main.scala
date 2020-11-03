package webapp


import io.grpc.stub.StreamObserver
import io.shad862.kafkafob.admin.proto.describeGroups.{DescribeGroupsReply, DescribeGroupsRequest, DescribeGroupsServiceGrpcWeb}
import org.scalajs.dom
import scalapb.grpc.Channels
import slinky.web.ReactDOM

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Main {
  def main(args: Array[String]): Unit = {

    val web = DescribeGroupsServiceGrpcWeb.stub(Channels.grpcwebChannel("http://localhost:8081"))
    import scala.scalajs.concurrent.JSExecutionContext.Implicits._

    //web.describeGroups(new DescribeGroupsRequest()).onComplete(println(_))

    web.describeGroupsReplying(new DescribeGroupsRequest(), new StreamObserver[DescribeGroupsReply] {
      override def onNext(value: DescribeGroupsReply): Unit = {
        handle(value)
      }
      override def onError(throwable: Throwable): Unit = {
        println(throwable)
      }
      override def onCompleted(): Unit = println("Buy")
    })

    IndexCSS
    ReactDOM.render(App.component(()), dom.document.getElementById("container"))
  }

  def handle(value: DescribeGroupsReply) = {
    print("handeled")
  }
}

@JSImport("./index.css", JSImport.Namespace)
@js.native
object IndexCSS extends js.Object
