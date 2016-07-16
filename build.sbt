import sbt.Keys._

lazy val finagram = (project in file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    Seq(
      name := "Finagram",
      version := "0.0.1",
      organization := "ru.dokwork",
      scalaVersion := "2.11.8",
      libraryDependencies ++= Seq(
        "org.slf4j"       %   "slf4j-api"     % "1.7.21",
        "ch.qos.logback"  % "logback-classic" % "1.1.7",
        "com.typesafe"    %   "config"        % "1.3.0",
        "org.json4s"      %%  "json4s-native" % "3.4.0",
        "com.twitter"     %%  "finagle-http"  % "6.35.0",
        "org.mockito"     %   "mockito-all"   % "1.9.5"     % "test",
        "org.scalatest"   %%  "scalatest"     % "3.0.0-M15" % "it,test"
      )
    )
  )
