package es.hybride

import scala.collection.immutable
import scala.io.Source
import Main._

object StreamMain {

  private def readFile(fileName: String): Stream[(Key, Option[DeviceType])] =
    Source.fromFile(fileName).getLines().map(dataExtractor).toStream

  def main(args: Array[String]): Unit = {
    var i = 0
  readFile("just1M.json").take(1000000).foreach(println)
    println(i)
    //Thread.sleep(2000)
  }

}
