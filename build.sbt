import sbt.Keys._

name := "finagram"
version := "0.3.0"
organization := "com.github.finagram"
scalaVersion := "2.11.8"
crossScalaVersions := Seq("2.11.8", "2.12.0")
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
resolvers += "jitpack" at "https://jitpack.io"
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "com.typesafe" % "config" % "1.3.0",
  "org.json4s" %% "json4s-native" % "3.4.0",
  "com.twitter" %% "finagle-http" % "6.41.0",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "uk.co.jemos.podam" % "podam" % "6.0.4.RELEASE" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.7" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)
pomExtra :=
  <developers>
    <developer>
      <id>dokwork</id>
      <name>Vladimir Popov</name>
      <url>http://dokwork.ru</url>
    </developer>
  </developers>
