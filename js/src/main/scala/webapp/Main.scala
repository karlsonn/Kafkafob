package webapp

import org.scalajs.dom
import slinky.web.ReactDOM

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Main {
  def main(args: Array[String]): Unit = {

    //import scala.scalajs.concurrent.JSExecutionContext.Implicits._
    //web.describeGroups(new DescribeGroupsRequest()).onComplete(println(_))

    val css = IndexCSS

    ReactDOM.render(KafkafobApp(), dom.document.getElementById("container"))
    //ReactDOM.render(Samples.component(()), dom.document.getElementById("container"))
  }
}

@JSImport("./index.css", JSImport.Namespace)
@js.native
object IndexCSS extends js.Object
