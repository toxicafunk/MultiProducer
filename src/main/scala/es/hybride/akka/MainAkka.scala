package es.hybride.akka


import java.nio.ByteBuffer
import java.nio.file.{FileSystem, FileSystems}
import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.serialization.ByteBufferSerializer

import akka.util.ByteString
import akka.{Done, NotUsed}
import com.typesafe.config.Config

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

import akka.stream._
import akka.stream.scaladsl._

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.concurrent.TimeUnit

object MainAkka {

  type Key = ByteString
  type DeviceType = ByteString

  def dataExtractor(line: ByteString): (Key, Option[DeviceType]) = {
    val keyIdx = line.indexOfSlice("id".getBytes()) + 5
    val deviceTypeIdx = line.indexOfSlice("\"devicetype\":".getBytes())
    val key = line.slice(keyIdx, keyIdx + 40)
    val deviceType =
      if (deviceTypeIdx == -1) None
      else {
        val deviceTypeEndIdx = line.indexOfSlice("\"".getBytes(), deviceTypeIdx + 15)
        Some(line.slice(deviceTypeIdx + 15, deviceTypeEndIdx))
      }

    (key, deviceType)
  }

  val ec: ExecutionContextExecutor =
    ExecutionContext.fromExecutor(Executors.newCachedThreadPool())
  val system: ActorSystem = ActorSystem.create()
  implicit val mat = Materializer(system)

  val fs: FileSystem = FileSystems.getDefault
  val source: String => Source[ByteString, Future[IOResult]] = (file: String) =>
    FileIO.fromPath(fs.getPath(file))
  val lines: Flow[ByteString, ByteString, NotUsed] = Framing.delimiter(
    ByteString(System.lineSeparator),
    10000,
    allowTruncation = true
  )

  val stream: String => Future[Done] = (filename: String) =>
    source(filename)
      .via(lines)
      .map(line => dataExtractor(line))
      .runWith(
        Sink.foreach(l =>
          println(s"${l._1.utf8String} -> ${l._2.fold("")(_.utf8String)}")
        )
      )

  //@Benchmark
  //@BenchmarkMode(Array(Mode.AverageTime))
  //@OutputTimeUnit(TimeUnit.MILLISECONDS)
  def program(filename: String): Unit =
    stream(filename)
      .andThen {
        case Failure(t) => println(t)
        case Success(v) => println("Done.")
      }(ExecutionContext.global)

  def main(args: Array[String]): Unit = {
    //val filename = args(0)
    val filename = "just5k.json"
    program(filename)
  }
}
