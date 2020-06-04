name := "MultiProducer"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-clients" % "2.5.0",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds"
)
 // "-Ypartial-unification")
enablePlugins(JmhPlugin)
