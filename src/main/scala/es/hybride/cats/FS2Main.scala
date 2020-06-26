package es.hybride.cats

import cats.effect.{Blocker, ExitCode, IO, IOApp, Resource}
import cats.implicits._
import fs2.{Stream, io, text}
import java.nio.file.Paths
import java.util.concurrent.Executors

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import MainCats._


import es.hybride._

object FS2Main extends IOApp {
  /*private val blockingExecutionContext =
    Resource.make(IO(ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))))(ec => IO(ec.shutdown()))*/

  val processMessages: String => Stream[IO, (Key, Option[DeviceType])] = (file: String) =>
    Stream.resource(Blocker[IO]).flatMap { blockingEC =>
      io.file
        .readAll[IO](Paths.get(file), blockingEC, 4096)
        .through(text.utf8Decode)
        .through(text.lines)
        //.evalMap(line => if (line.isEmpty()) IO("", None) else dataExtractor(line)) // `map` if non-effectful`
        .mapAsyncUnordered(6)(line => if (line.isEmpty()) IO("", None) else dataExtractor(line))
    }

  def run(args: List[String]): IO[ExitCode] = {
    //val filename = args(0)
    val filename = "just5k.json"
    val t0 = System.currentTimeMillis()
    processMessages(filename)
      .map(tup => {println(s"${tup._1} -> ${tup._2}"); tup})
      .compile
      .drain
      .map(_ => {
        val t1 = System.currentTimeMillis()
        println("Elapsed time: " + (t1 - t0) + "ms")
      })
      .as(ExitCode.Success)
  }
}
