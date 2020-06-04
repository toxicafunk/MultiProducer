name := "MultiProducer"

version := "0.1"

scalaVersion := "2.13.1"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds"
)
 // "-Ypartial-unification")

val zioVersion        = "1.0.0-RC20"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"                    % zioVersion,
  "dev.zio"       %% "zio-streams"            % zioVersion,
)
