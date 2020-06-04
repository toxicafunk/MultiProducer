package es.hybride.monix

import java.io.{BufferedReader, FileInputStream, InputStreamReader}

import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.atomic.Atomic
import monix.reactive.{Consumer, Observable}

import scala.concurrent.Await
import scala.concurrent.duration._
import monix.execution.Callback

object MainMonix {

  type Key = String
  type DeviceType = String

  implicit val scheduler: Scheduler = monix.execution.Scheduler.global

  def dataExtractor(line: String): (Key, Option[DeviceType]) = {
    val keyIdx = line.indexOf("id") + 5
    val deviceTypeIdx = line.indexOf("\"devicetype\":")
    val key = line.substring(keyIdx, keyIdx + 40)
    val deviceType: Option[String] =
      if (deviceTypeIdx == -1) None
      else {
        val deviceTypeEndIdx = line.indexOf("\"", deviceTypeIdx + 15)
        Some(line.substring(deviceTypeIdx + 15, deviceTypeEndIdx))
      }

    (key, deviceType)
  }

  val readFile: String => Observable[String] = (file: String) => {
    val reader = Task(new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8")))
    Observable.fromLinesReader(reader)
  }

  def main(args: Array[String]): Unit = {
    //val filename = args(0)
    val filename = "just100k.json"
    val t0 = System.currentTimeMillis()

    val source = readFile(filename)
      .consumeWith(Consumer.foreachParallel(8)(line => {
        val cancelable = Task(dataExtractor(line)).runAsync(Callback.fromTry(println))
      }))

    //val cancelable = source.runToFuture(Scheduler.global)
    //Await.result(cancelable, 2.minute)
    source.runSyncUnsafe(5.seconds)
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) + "ms")
  }
}
