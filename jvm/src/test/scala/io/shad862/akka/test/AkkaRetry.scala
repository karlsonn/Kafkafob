package io.shad862.akka.test

import java.util.concurrent.{ExecutorService, Executors}

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import utest._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._


object AkkaRetry extends TestSuite {
  override def tests: Tests = Tests {

    implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(2))
    implicit val as = ActorSystem(Behaviors.empty, "simple")

    val delayed = akka.pattern.after(200.milliseconds)(Future.failed(new IllegalStateException("boom")))

    val future = Future { Thread.sleep(204); println("I'm done"); "value" }
    val result = Future.firstCompletedOf(Seq(future, delayed))

    val res = Await.result(result, 205.milliseconds)
    println(res)

  }
}
