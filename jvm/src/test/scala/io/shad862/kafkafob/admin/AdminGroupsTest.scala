package io.shad862.kafkafob.admin

import com.typesafe.config.ConfigFactory
import utest._

object AdminGroupsTest extends TestSuite {

  val tests: Tests = Tests {

    val config = ConfigFactory.load()
    val servers = config.getString("kafkaBootstrapServers")
    assume(servers != null)
    val kafkaAdmin = KafkafobAdmin(servers)

    test {
      val nodes = kafkaAdmin.describeNodes()
      assert (nodes.size() > 0)
      nodes.size()
    }

    test {
      val listings = kafkaAdmin.listTopics()
      assert (listings.size() > 0)
    }

    test {
      val groups = kafkaAdmin.describeGroups()
      assert(groups.size() > 0)
    }
  }
}
