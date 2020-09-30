package io.shad862.kafkafob

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import io.shad862.kafkafob.admin.proto.DescribeGroupsServiceHandler
import io.shad862.kafkafob.admin.services.SimpleDescribeGroupService

import scala.concurrent.{ExecutionContext, Future}

object SimpleDescribeGroupServer {
  val config = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on").withFallback(ConfigFactory.defaultApplication())
  implicit val system = ActorSystem("Server-System", config)
  def main(args: Array[String]): Unit = {
    new SimpleDescribeGroupServer().run()
  }
}

class SimpleDescribeGroupServer(implicit system: ActorSystem) {
  def run(): Future[Http.ServerBinding] = {
    implicit val materializer:Materializer = ActorMaterializer()
    implicit val context: ExecutionContext = system.dispatcher

    val service: HttpRequest => Future[HttpResponse] = DescribeGroupsServiceHandler(new SimpleDescribeGroupService())

    val binding = Http().bindAndHandleAsync(
      service,
      interface = "127.0.0.1",
      port = 8080,
      connectionContext = HttpConnectionContext())

    binding.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }

    binding
  }
}
