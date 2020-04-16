package es.hybride

import scala.collection.immutable
import scala.io.Source

object StreamMain {

  type Key = String
  type DeviceType = String

  private def dataExtractor(line: String): (Key, Option[DeviceType]) = {
    val keyIdx = line.indexOf("id") + 5
    val deviceTypeIdx = line.indexOf("\"devicetype\":") + 15
    val deviceTypeEndIdx = line.indexOf("\"", deviceTypeIdx)
    val key = line.substring(keyIdx, keyIdx + 40)
    val deviceType: Option[String] = if (deviceTypeIdx != -1)
      Some(line.substring(deviceTypeIdx, deviceTypeEndIdx))
      else None
    (key, deviceType)
  }

  private def readFile(fileName: String): Stream[(Key, Option[DeviceType])] =
    Source.fromFile(fileName).getLines().map(dataExtractor).toStream

  def main(args: Array[String]): Unit = {
    var i = 0
  readFile("just1M.json").take(1000000).foreach(println)
    println(i)
    //Thread.sleep(2000)
  }

}
