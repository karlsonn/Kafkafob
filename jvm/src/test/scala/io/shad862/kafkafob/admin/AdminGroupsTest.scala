package io.shad862.kafkafob.admin

import com.typesafe.config.ConfigFactory
import utest._

object AdminGroupsTest extends TestSuite {

  private val config = ConfigFactory.load()
  private val servers = config.getString("kafkaBootstrapServers")
  assume(servers != null)

  val tests: Tests = Tests {

    test("describe nodes of cluster") {
      assert (Admin.describeNodes(servers).size() > 0)
    }

    test("list all topics") {
      assert (Admin.listTopics(servers).size() > 0)
    }
  }
}
