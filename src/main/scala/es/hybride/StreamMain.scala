package es.hybride

import scala.collection.immutable
import scala.io.Source
import Main._

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State

import java.util.concurrent.TimeUnit
import scala.collection.parallel.CollectionConverters._

class StreamMain {

  def readFile(fileName: String): LazyList[(Key, Option[DeviceType])] =
    Source.fromFile(fileName).getLines().to(LazyList).map(dataExtractor)

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def program(): Unit = readFile("just100k.json").foreach(Main.proccessMessage)

  @Benchmark
  @BenchmarkMode(Array(Mode.AverageTime))
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  def programPar(): Unit = readFile("just100k.json")
    .toList
    .par
    .foreach(Main.proccessMessage)
}
object StreamMain {

  def main(args: Array[String]): Unit = {
    val p = new StreamMain()
    //p.program()
    p.programPar()
    println(s"Successes: $successes")
  }

}
