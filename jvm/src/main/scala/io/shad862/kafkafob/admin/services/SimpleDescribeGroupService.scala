package io.shad862.kafkafob.admin.services

import akka.NotUsed
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import io.shad862.kafkafob.admin.proto.{ConsumerGroupDescriptor, DescribeGroupsReply, DescribeGroupsRequest, DescribeGroupsService}

import scala.concurrent.duration.DurationInt
import scala.concurrent.Future

class SimpleDescribeGroupService(implicit mat: Materializer) extends DescribeGroupsService {
  import mat.executionContext
  override def describeGroups(in: DescribeGroupsRequest): Future[DescribeGroupsReply] = {
    Future.successful(DescribeGroupsReply(Seq(ConsumerGroupDescriptor("Single group"))))
  }
  override def describeGroupsReplying(in: DescribeGroupsRequest): Source[DescribeGroupsReply, NotUsed] = {
    Source(1 to 5).map(value => DescribeGroupsReply(Seq(ConsumerGroupDescriptor(s"Group $value")))).throttle(1, 2 seconds)
  }
}
