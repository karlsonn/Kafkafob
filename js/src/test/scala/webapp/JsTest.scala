package webapp

import com.trueaccord.advanced._
import io.shad862.kafkafob.admin.proto.describeGroups.{ConsumerGroupDescriptor, DescribeGroupsProto}
import utest._

object JsTest extends TestSuite {
  val tests = Tests {
    val grop = ConsumerGroupDescriptor().update(_.groupId := "Group Id")
    'updateWorks {
      assert(grop.update(_.groupId := "Another Group Id") == grop.copy(groupId = "Another Group Id"))
    }
    'parseFromIsInverseOfByteArray {
      assert(ConsumerGroupDescriptor.parseFrom(grop.toByteArray) == grop)
    }
    'customOptionWorks {
      assert(
        ConsumerGroupDescriptor.scalaDescriptor.findFieldByName("groupId").get.getOptions.extension(DescribeGroupsProto.option1) == "option one")
    }
  }
}
