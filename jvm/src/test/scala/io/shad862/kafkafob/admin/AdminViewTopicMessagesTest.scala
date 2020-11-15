package io.shad862.kafkafob.admin

import com.typesafe.config.ConfigFactory
import utest._

object AdminViewTopicMessagesTest extends TestSuite {

  val tests: Tests = Tests {
    val config = ConfigFactory.load()
    val servers = config.getString("kafkaBootstrapServers")
    assume(servers != null)
    val kafkaAdmin = KafkafobAdmin(servers)

    test {
      kafkaAdmin.listTopicMessages("Some topic")
    }
  }
}
