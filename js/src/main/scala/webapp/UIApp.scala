package webapp

import io.grpc.stub.StreamObserver
import io.shad862.kafkafob.admin.proto.describeGroups.{DescribeGroupsReply, DescribeGroupsRequest, DescribeGroupsServiceGrpcWeb}
import org.scalajs.dom
import scalapb.grpc.Channels
import webapp.css.AppCSS
import webapp.routes.AppRouter

import scala.scalajs.js.annotation.JSExport


object UIApp {
  @JSExport
  def main2(args: Array[String]): Unit = {
    val web = DescribeGroupsServiceGrpcWeb.stub(Channels.grpcwebChannel("http://localhost:8081"))
    import scala.scalajs.concurrent.JSExecutionContext.Implicits._

    web.describeGroups(new DescribeGroupsRequest()).onComplete(println(_))

    web.describeGroupsReplying(new DescribeGroupsRequest(), new StreamObserver[DescribeGroupsReply] {
      override def onNext(value: DescribeGroupsReply): Unit = println(value)
      override def onError(throwable: Throwable): Unit = println(throwable)
      override def onCompleted(): Unit = println("Buy")
    })

    AppCSS.load
    AppRouter.router().renderIntoDOM(dom.document.getElementById("content"))
  }
}