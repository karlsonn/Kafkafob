package io.shad862.kafkafob.admin.services

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import io.shad862.kafkafob.admin.proto.{ConsumerGroupDescriptor, DescribeGroupsReply, DescribeGroupsRequest, DescribeGroupsService}

import scala.concurrent.{ExecutionContext, Future}

class SimpleDescribeGroupService(implicit mat: Materializer) extends DescribeGroupsService {
  import mat.executionContext

  override def describeGroups(in: DescribeGroupsRequest): Future[DescribeGroupsReply] = {
    Future.successful(DescribeGroupsReply().addDescriptors(ConsumerGroupDescriptor().withGroupId("id")))
  }

  override def describeGroupsReplying(in: DescribeGroupsRequest): Source[DescribeGroupsReply, NotUsed] = {
    val reply = DescribeGroupsReply().addDescriptors(ConsumerGroupDescriptor().withGroupId("id"))
    Source(Seq(reply))
  }
}
