package io.shad862.kafkafob.admin

import com.typesafe.config.ConfigFactory
import utest._

object AdminGroupsTest extends TestSuite {

  val tests: Tests = Tests {
    val config = ConfigFactory.load()
    val servers = config.getString("kafkaBootstrapServers")
    assume(servers != null)

    test {
      val nodes = AdminUitls.describeNodes(servers)
      assert (nodes.size() > 0)
      nodes.size()
    }

    test {
      val listings = AdminUitls.listTopics(servers)
      assert (listings.size() > 0)
    }
  }
}
