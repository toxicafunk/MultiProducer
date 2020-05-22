package es.hybride

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import monix.reactive.Consumer
import Main._

object LoadBalancer {

  val filename = "just100k.json"

  val extractorConsumer = Consumer.foreach[String](line => {
    val tup = dataExtractor(line)
    println(tup)
  })

  val loadBalancer = Consumer
      .loadBalance(parallelism = 10, extractorConsumer)

  val observable = readFile(filename)

  val task = observable.consumeWith(loadBalancer)

  def main(args: Array[String]): Unit = {
    val t0 = System.currentTimeMillis()

    task.runSyncUnsafe(5.seconds)

    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) + "ms")
  }
}
