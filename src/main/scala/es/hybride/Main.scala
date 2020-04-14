package es.hybride

import scala.io.Source

object Main {

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

  private def readFile(fileName: String) = //: List[(Key, Option[DeviceType])] =
    Source.fromFile(fileName).getLines().map(dataExtractor).toList

  def main(args: Array[String]): Unit = {
    readFile("just5k.json").take(5).map(println)
  }

}
