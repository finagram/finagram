name := "finagram"
version := "0.3.0"
organization := "com.github.finagram"
scalaVersion := "2.11.8"
crossScalaVersions := Seq("2.11.11", "2.12.3")
scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings")
libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.21",
  "com.typesafe" % "config" % "1.3.0",
  "org.json4s" %% "json4s-native" % "3.4.0",
  "com.twitter" %% "finagle-http" % "6.41.0",
  // for tests:
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.7" % "test",
  "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.5" % "test"
)
pomExtra :=
  <developers>
    <developer>
      <id>dokwork</id>
      <name>Vladimir Popov</name>
      <url>http://www.dokwork.ru</url>
    </developer>
  </developers>
