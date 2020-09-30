package io.shad862.kafkafob.admin

import com.typesafe.config.ConfigFactory
import utest._

object DescribeGroupsTest extends TestSuite {
  val tests: Tests = Tests {
    val servers = ConfigFactory.load().getString("kafkaBootstrapServers")
    test {
      val groups = AdminUitls.describeGroups(servers)
      assert(groups.size() > 0)
    }
  }
}