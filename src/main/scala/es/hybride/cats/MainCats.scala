package es.hybride.cats

import cats.implicits._
import scala.io.Source
import cats.effect.{ExitCode, IO, IOApp}

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import java.util.concurrent.TimeUnit

import es.hybride._

object MainCats extends IOApp {

  def dataExtractor(line: String): IO[(Key, Option[DeviceType])] = IO {
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

  private def readFile(fileName: String): IO[List[String]] =
    IO(Source.fromFile(fileName).getLines().toList)

  //@Benchmark
  //@BenchmarkMode(Array(Mode.AverageTime))
  //@OutputTimeUnit(TimeUnit.MILLISECONDS)
  def program(filename: String) =
    for {
      iol <- readFile(filename)
      ext <- iol.traverse(s => dataExtractor(s))
    } yield {
      ext.map(println)
    }

  override def run(args: List[String]): IO[ExitCode] = {
    val filename = "just5k.json"
    program(filename).as(ExitCode.Success)
  }
}
