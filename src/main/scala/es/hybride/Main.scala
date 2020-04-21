package es.hybride

import scala.io.Source

object Main {

  type Key = String
  type DeviceType = String

  def dataExtractor(line: String): (Key, Option[DeviceType]) = {
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

  private def readFile(fileName: String): List[(Key, Option[DeviceType])] = //: List[(Key, Option[DeviceType])] =
    Source.fromFile(fileName).getLines().map(dataExtractor).toList

  def main(args: Array[String]): Unit = {
    readFile("just1M.json").foreach(println)
  }

}
