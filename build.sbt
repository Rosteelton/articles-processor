ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

val zioVersion            = "2.0.13"
val zioConfigVersion      = "3.0.7"
val logbackClassicVersion = "1.4.7"
val zioLoggingVersion     = "2.1.12"
val sttpZioVersion        = "3.8.15"
val sttpSlf4jVersion      = "3.8.15"
val catsVersion           = "2.9.0"
val refinedVersion        = "0.10.3"

lazy val root = (project in file("."))
  .settings(
    name := "coding-challenge",
    libraryDependencies ++= Seq(
      "dev.zio"                       %% "zio"                 % zioVersion,
      "dev.zio"                       %% "zio-streams"         % zioVersion,
      "dev.zio"                       %% "zio-config"          % zioConfigVersion,
      "dev.zio"                       %% "zio-config-magnolia" % zioConfigVersion,
      "dev.zio"                       %% "zio-config-typesafe" % zioConfigVersion,
      "dev.zio"                       %% "zio-logging"         % zioLoggingVersion,
      "dev.zio"                       %% "zio-logging-slf4j"   % zioLoggingVersion,
      "com.softwaremill.sttp.client3" %% "zio"                 % sttpZioVersion,
      "com.softwaremill.sttp.client3" %% "slf4j-backend"       % sttpSlf4jVersion,
      "ch.qos.logback"                 % "logback-classic"     % logbackClassicVersion,
      "org.typelevel"                 %% "cats-core"           % catsVersion,
      "eu.timepit"                    %% "refined"             % refinedVersion,
      "dev.zio"                       %% "zio-test-sbt"        % zioVersion % Test
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
