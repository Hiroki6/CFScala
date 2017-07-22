name := "MatrixFactorization"

version := "1.0"

scalaVersion := "2.12.2"

// https://mvnrepository.com/artifact/org.apache.commons/commons-io
libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.scalanlp" %% "breeze" % "0.13.1",
  "org.scalanlp" %% "breeze-natives" % "0.13.1",
  "org.scalanlp" %% "breeze-viz" % "0.13.1",
  "org.scala-sbt" % "io_2.11" % "1.0.0-M1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
)
