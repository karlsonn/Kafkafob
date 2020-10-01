package io.shad862.kafkafob.admin

import java.util
import java.util.Properties
import java.util.stream.Collectors

import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, ConsumerGroupDescription, ConsumerGroupListing, TopicListing}
import org.apache.kafka.common.Node

class KafkafobAdmin(servers: String) {

  val props = new Properties()
  props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, servers)
  private val client: AdminClient = AdminClient.create(props)

  def describeNodes(): util.Collection[Node] = {
    client.describeCluster().nodes().get
  }

  def listTopics(): util.Collection[TopicListing] = {
    client.listTopics().listings().get()
  }

  def describeGroups(): util.Map[String, ConsumerGroupDescription] = {
    client.listConsumerGroups().all()
      .thenApply((listings: util.Collection[ConsumerGroupListing]) =>
        listings.stream().map((listing: ConsumerGroupListing) => listing.groupId()).collect(Collectors.toList()))
      .thenApply((ids: util.Collection[String]) => client.describeConsumerGroups(ids).all()).get().get()
  }

  def listTopicMessages(topic: String) = {
  }
}

object KafkafobAdmin {
  def apply(servers: String) = new KafkafobAdmin(servers)
}
