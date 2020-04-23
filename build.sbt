name := "MultiProducer"

version := "0.1"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.4"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Ypartial-unification")
