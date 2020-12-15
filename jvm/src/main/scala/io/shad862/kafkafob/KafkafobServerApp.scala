package io.shad862.kafkafob

import akka.actor.ActorSystem
import akka.grpc.scaladsl.WebHandler
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.{Http, HttpConnectionContext}
import com.typesafe.config.ConfigFactory
import io.shad862.kafkafob.admin.proto.DescribeGroupsServiceHandler
import io.shad862.kafkafob.admin.services.SimpleDescribeGroupService

import scala.concurrent.{ExecutionContext, Future}

object KafkafobServerApp {
  val config = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on").withFallback(ConfigFactory.defaultApplication())
  implicit val system = ActorSystem("Server-System", config)
  def main(args: Array[String]): Unit = {
    new KafkafobServerApp().run()
  }
}

class KafkafobServerApp(implicit system: ActorSystem) {
  def run(): Future[Http.ServerBinding] = {
    implicit val context: ExecutionContext = system.dispatcher

    val gRPCService: HttpRequest => Future[HttpResponse] = DescribeGroupsServiceHandler(new SimpleDescribeGroupService())
    val gRPCBinding = Http().newServerAt("127.0.0.1", 8082).bind(gRPCService)

    val gRPCWebService = WebHandler.grpcWebHandler { case request: HttpRequest => gRPCService.apply(request) }
    val gRPCWebBinding = Http().newServerAt("127.0.0.1", 8081).bind(gRPCWebService)

    gRPCBinding.foreach { binding => println(s"gRPC server[gRPC binding] bound to: ${binding.localAddress}") }
    gRPCWebBinding.foreach { binding => println(s"gRPCWeb server[gRPC web binding] bound to: ${binding.localAddress}") }

    gRPCBinding
  }
}
