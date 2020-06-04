package es.hybride.monix

import java.io.{BufferedReader, FileInputStream, InputStreamReader}

import monix.eval.Task
import monix.execution.{Ack, CancelableFuture}
import monix.execution.Ack.Continue
import monix.reactive.{Consumer, Observable, Observer}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import MainMonix._

object MainTask {

  val filename = "just100k.json"

  // Parallelism on Producer side
  val tasks = MainMonix
    .readFile(filename)
    .mapParallelUnordered(parallelism = 10)(line => Task(dataExtractor(line)))

  def main(args: Array[String]): Unit = {
    val t0 = System.currentTimeMillis()

    tasks.foreachL(println).runSyncUnsafe(5.seconds)

    val t1 = System.currentTimeMillis()
    Task(println("Elapsed time: " + (t1 - t0) + "ms"))
  }
}
