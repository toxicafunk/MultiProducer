package es.hybride

import cats.implicits._
import scala.io.Source
import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {

  type Key = String
  type DeviceType = String

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

  /*private def processList(lines: List[String]): IO[List[(Key, Option[DeviceType])]] =
    IO(lines.map(dataExtractor))*/

  private def readFile(fileName: String): IO[List[String]] =
    IO(Source.fromFile(fileName).getLines().toList)

  /*def main(args: Array[String]): Unit = {
    val program = for {
      iol <- readFile("just5k.json")
      ext  <- iol.traverse(s => dataExtractor(s))
    } yield {
      ext.take(5).map(println)
    }

    program.unsafeRunSync()
  }*/

  override def run(args: List[String]): IO[ExitCode] = {
    val program = for {
      iol <- readFile("just5k.json")
      ext <- iol.traverse(s => dataExtractor(s))
    } yield {
      ext.map(println)
      //ext.take(5).map(println)
    }

    program.as(ExitCode.Success)
  }
}
