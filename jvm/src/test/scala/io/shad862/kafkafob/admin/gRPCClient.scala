package io.shad862.kafkafob.admin

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.stream.ActorMaterializer
import io.shad862.kafkafob.admin.proto.{DescribeGroupsRequest, DescribeGroupsServiceClient}

import scala.concurrent.Await._
import scala.concurrent.duration._


object gRPCClient {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("HelloWorldClient")
    implicit val materializer = ActorMaterializer()
    implicit val contextExecutor = system.dispatcher

    val clientSettings = GrpcClientSettings.connectToServiceAt("127.0.0.1", 8080).withTls(false)

    val client: DescribeGroupsServiceClient = DescribeGroupsServiceClient(clientSettings)
    val response = result(client.describeGroups(DescribeGroupsRequest()), Duration.Inf)
    println(response)

    //client.describeGroupsReplying(DescribeGroupsRequest()).to(Sink.foreach(println))
  }
}