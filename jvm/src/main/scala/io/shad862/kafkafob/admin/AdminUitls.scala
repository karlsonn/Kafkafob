package io.shad862.kafkafob.admin

import java.util
import java.util.Properties
import java.util.stream.Collectors
import java.util.stream.Collectors.toList

import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, ConsumerGroupDescription, ConsumerGroupListing, TopicListing}
import org.apache.kafka.common.Node

object AdminUitls {
  def describeNodes(servers: String): util.Collection[Node] = {
    val props = new Properties()
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, servers)
    AdminClient.create(props).describeCluster().nodes().get
  }

  def listTopics(servers: String): util.Collection[TopicListing] = {
    val props = new Properties()
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, servers)
    AdminClient.create(props).listTopics().listings().get()
  }

  def describeGroups(servers: String): util.Map[String, ConsumerGroupDescription] = {
    val props = new Properties()
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, servers)


    val client = AdminClient.create(props)

    client.listConsumerGroups().all()
      .thenApply((listings: util.Collection[ConsumerGroupListing]) =>
        listings.stream().map((listing: ConsumerGroupListing) => listing.groupId()).collect(Collectors.toList()))
      .thenApply((ids: util.Collection[String]) => client.describeConsumerGroups(ids).all()).get().get()
  }
}
