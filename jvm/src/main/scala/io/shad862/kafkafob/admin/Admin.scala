package io.shad862.kafkafob.admin

import java.util
import java.util.Properties

import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, TopicListing}
import org.apache.kafka.common.Node

object Admin {
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
}
