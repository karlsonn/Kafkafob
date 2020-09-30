package io.shad862.kafkafob.admin

import java.lang.Thread

import com.typesafe.config.ConfigFactory
import utest._
import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.grpc.GrpcClientSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import io.shad862.kafkafob.admin.proto.{DescribeGroupsRequest, DescribeGroupsServiceClient}

import scala.concurrent.Await._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object DescribeGroupsTest extends TestSuite {
  val tests: Tests = Tests {
    val servers = ConfigFactory.load().getString("kafkaBootstrapServers")
    test {
      val groups = AdminUitls.describeGroups(servers)
      assert(groups.size() > 0)
    }
  }
}



object GreeterClient {
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