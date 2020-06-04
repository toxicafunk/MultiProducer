package es.hybride

import scala.io.Source
import zio.{App, Runtime, Task, ZIO}
import zio.{ExitCode, ZEnv, ZIO}
import zio.console._

object Main {

  val rt = Runtime.unsafeFromLayer(Console.live)

  def main(args: Array[String]): Unit = {
    val prog = readFile("just1M.json").map(ttup => ttup.flatMap(tup => putStrLn(s"${tup._1} -> ${tup._2.getOrElse("")}")))
    rt.unsafeRun(ZIO.collectAll_(prog.iterator.to(Iterable)))
  }

    /*rt.unsafeRun {
      for {
        lTup <- ZIO.collectAll(readFile("just100k.json"))
        _   <- ZIO.foreach(lTup)(tup => putStrLn(s"${tup._1} -> ${tup._2.getOrElse("")}"))
      } yield ()
    */
      /*for {
        l <- readFile("just1M.json")
        _ <- ZIO.foreach(l)(tup => putStrLn(s"${tup._1} -> ${tup._2.getOrElse("")}"))
      } yield ()*/

  type Key = String
  type DeviceType = String

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

  private def readFile(
      fileName: String
  ): Iterator[Task[(Key, Option[DeviceType])]] =
    Source.fromFile(fileName).getLines().map(dataExtractor)

  /*def main(args: Array[String]): Unit = {
    Task.collectAll(readFile("just1M.json"))
      //.foreach(println)
  }*/

}
