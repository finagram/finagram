import sbt.Keys._

lazy val finagram = (project in file("."))
  .configs(IntegrationTest)
  .settings(Defaults.itSettings: _*)
  .settings(
    Seq(
      name := "finagram",
      version := "0.1.0",
      organization := "ru.finagram",
      scalaVersion := "2.11.8",
      scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings"),
      libraryDependencies ++= Seq(
        "org.slf4j"       %   "slf4j-api"       % "1.7.21",
        "com.typesafe"    %   "config"          % "1.3.0",
        "org.json4s"      %%  "json4s-native"   % "3.4.0",
        "com.twitter"     %%  "finagle-http"    % "6.35.0",
        "org.mockito"     %   "mockito-all"     % "1.9.5"           % "test",
        "uk.co.jemos.podam" % "podam"           % "6.0.4.RELEASE"   % "test",
        "ch.qos.logback"  %   "logback-classic" % "1.1.7"           % "it,test",
        "org.scalatest"   %%  "scalatest"       % "3.0.0"           % "it,test"
      ),
      pomExtra :=
        <developers>
          <developer>
            <id>dokwork</id>
            <name>Vladimir Popov</name>
            <url>http://dokwork.ru</url>
          </developer>
        </developers>
    )
  )
