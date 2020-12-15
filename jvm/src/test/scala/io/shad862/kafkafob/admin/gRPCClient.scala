package io.shad862.kafkafob.admin

import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.stream.scaladsl.Sink
import io.shad862.kafkafob.admin.proto.{DescribeGroupsRequest, DescribeGroupsServiceClient}


object gRPCClient {
  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("gRPCActorSystem")
    implicit val contextExecutor = system.dispatcher

    val client: DescribeGroupsServiceClient = DescribeGroupsServiceClient(GrpcClientSettings.connectToServiceAt("127.0.0.1", 8082).withTls(false))

    client.describeGroups(DescribeGroupsRequest()).onComplete(println(_))
    client.describeGroupsReplying(DescribeGroupsRequest()).runWith(Sink.foreach(println(_)))
  }
}