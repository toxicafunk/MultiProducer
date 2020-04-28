name := "MultiProducer"

version := "0.1"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
  "io.monix" %% "monix" % "3.2.0"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Ypartial-unification")
