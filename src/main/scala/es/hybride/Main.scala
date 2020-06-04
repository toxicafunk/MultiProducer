package es.hybride

import scala.io.Source

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

import java.util.concurrent.TimeUnit
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import java.{util => ju}
import ju.concurrent.Future
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import scala.util.Random

//import scala.collection.parallel.CollectionConverters._
import java.util.concurrent.atomic.AtomicLongArray
import java.util.concurrent.atomic.AtomicInteger

//jmh:run -i 3 -wi 3 -f1 -t1 es.hybride.*.*
class Main {
  import Main._

  def readFile(fileName: String): Iterator[String] = //: List[(Key, Option[DeviceType])] =
    Source.fromFile(fileName).getLines()

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def program(): Unit =
    readFile("just100k.json")
      .map(dataExtractor)
      .foreach(proccessMessage)

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def programPar(): Unit = {
    val its = readFile("just100k.json")
      .map(dataExtractor)
      .toList
      .par

    its.foreach(proccessMessage)
  }
}

object Main {

  type Key = String
  type DeviceType = String

  val props = new ju.Properties()
  props.put(
    "bootstrap.servers",
    "172.18.0.2:9092,172.18.0.4:9092,172.18.0.5:9092"
  );
  props.put("acks", "all");
  props.put(
    "key.serializer",
    "org.apache.kafka.common.serialization.StringSerializer"
  );
  props.put(
    "value.serializer",
    "org.apache.kafka.common.serialization.StringSerializer"
  );

  val producer: Producer[String, String] = new KafkaProducer(props);

  var successes = 0

  def send(key: String, msg: String): Future[RecordMetadata] =
    producer.send(new ProducerRecord[String, String]("my-topic", key, msg))

  def proccessMessage(tup: (Key, Option[DeviceType])) = {
    val msg = tup._2
    if (msg.isDefined) {
      val rmd: RecordMetadata = send(tup._1, msg.get).get(1, TimeUnit.SECONDS)
      successes += 1
      println(s"${rmd.partition()} ${rmd.offset()}")
    } else ()
  }

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

  def main(args: Array[String]): Unit = {
    val m = new Main()
    //m.program()
    m.programPar()
    println(s"Successes: $successes")
  }

}
