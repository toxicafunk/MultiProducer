package es.hybride

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits._

import scala.io.Source

object CatsResource extends IOApp {

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
    Resource.make(IO(Source.fromFile(fileName)))(res => IO(res.close()))

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
    val res = readFile("just5k.json")
    val program = for {
      iol <- res.use(_.getLines().toList.traverse(s => dataExtractor(s)))
    } yield iol.take(5).map(println)

      program.as(ExitCode.Success)
  }
}
