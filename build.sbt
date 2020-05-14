name := "MultiProducer"

version := "0.1"

scalaVersion := "2.12.11"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "1.3.0" withSources() withJavadoc(),
  // available for 2.12, 2.13
  "co.fs2" %% "fs2-core" % "2.2.1", // For cats 2 and cats-effect 2
  // optional I/O library
  "co.fs2" %% "fs2-io" % "2.2.1"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds",
  "-Ypartial-unification")
