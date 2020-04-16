package es.hybride

import cats.implicits._
import scala.io.Source
import cats.effect.{ExitCode, IO, IOApp}

object Main extends { //IOApp {

  type Key = String
  type DeviceType = String

  private def dataExtractor(line: String): IO[(Key, Option[DeviceType])] = IO {
    val keyIdx = line.indexOf("id") + 5
    val deviceTypeIdx = line.indexOf("\"devicetype\":") + 15
    val deviceTypeEndIdx = line.indexOf("\"", deviceTypeIdx)
    val key = line.substring(keyIdx, keyIdx + 40)
    val deviceType: Option[String] = if (deviceTypeIdx != -1)
      Some(line.substring(deviceTypeIdx, deviceTypeEndIdx))
      else None
    (key, deviceType)
  }

  /*private def processList(lines: List[String]): IO[List[(Key, Option[DeviceType])]] =
    IO(lines.map(dataExtractor))*/


  private def readFile(fileName: String) = //: List[(Key, Option[DeviceType])] =
    IO(Source.fromFile(fileName).getLines().toList)

  def main(args: Array[String]): Unit = {
    val program = for {
      iol <- readFile("just5k.json")
      ext  <- iol.traverse(s => dataExtractor(s))
    } yield {
      ext.take(5).map(println)
    }

    program.unsafeRunSync()
  }


}
