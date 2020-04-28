package es.hybride

import java.io.{BufferedReader, FileInputStream, InputStreamReader}

import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.atomic.Atomic
import monix.reactive.{Consumer, Observable}

import scala.concurrent.Await
import scala.concurrent.duration._

object Main {

  type Key = String
  type DeviceType = String

  implicit val scheduler: Scheduler = monix.execution.Scheduler.global

  def dataExtractor(line: String): Task[(Key, Option[DeviceType])] = Task {
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

  val ref: Atomic[Int] = Atomic(0)

  def main(args: Array[String]): Unit = {
    //val filename = args(0)
    val filename = "just5k.json"
    val t0 = System.currentTimeMillis()
    val source = readFile(filename)
      .map(line => {
        ref.getAndTransform(_+1)
        dataExtractor(line)
      })
      .consumeWith(Consumer.foreachParallel(8)(task => {
        task.runToFuture(Scheduler.global).onComplete(_ => ref.getAndTransform(_-1))
      }))
    val cancelable = source.runToFuture(Scheduler.global)
    Await.result(cancelable, 2.minute)
    while (ref.get() > 0) {}
    val t1 = System.currentTimeMillis()
    println("Elapsed time: " + (t1 - t0) + "ms")
  }
}
