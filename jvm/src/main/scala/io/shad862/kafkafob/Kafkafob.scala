package io.shad862.kafkafob

import akka.actor.ActorSystem
import akka.grpc.scaladsl.WebHandler
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, HttpConnectionContext}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import io.shad862.kafkafob.admin.proto.DescribeGroupsServiceHandler
import io.shad862.kafkafob.admin.services.SimpleDescribeGroupService

import scala.concurrent.{ExecutionContext, Future}

object Kafkafob {
  val config = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on").withFallback(ConfigFactory.defaultApplication())
  implicit val system = ActorSystem("Server-System", config)
  def main(args: Array[String]): Unit = {
    new Kafkafob().run()
  }
}

class Kafkafob(implicit system: ActorSystem) {
  def run(): Future[Http.ServerBinding] = {
    implicit val context: ExecutionContext = system.dispatcher
    implicit val materializer: Materializer = ActorMaterializer()

    val gRPCService: HttpRequest => Future[HttpResponse] = DescribeGroupsServiceHandler(new SimpleDescribeGroupService())
    val gRPCBinding = Http().bindAndHandleAsync(gRPCService, "127.0.0.1", 8080, HttpConnectionContext())

    val gRPCWebService = WebHandler.grpcWebHandler { case request: HttpRequest => gRPCService.apply(request) }
    val gRPCWebBinding = Http().bindAndHandleAsync(gRPCWebService, "127.0.0.1", 8081, HttpConnectionContext())

    gRPCBinding.foreach { binding => println(s"gRPC server[gRPC binding] bound to: ${binding.localAddress}") }
    gRPCWebBinding.foreach { binding => println(s"gRPC server[gRPC binding] bound to: ${binding.localAddress}") }

    gRPCBinding
  }
}
