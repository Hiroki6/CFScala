name := "CFScala"

lazy val commonSettings = Seq(
  organization := "com.recommendations.collaborative_filtering",
  version := "1.0",
  scalaVersion := "2.12.2"
)



// https://mvnrepository.com/artifact/org.apache.commons/commons-io
lazy val dependencies = Seq(
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.scalanlp" %% "breeze" % "0.13.1",
  "org.scalanlp" %% "breeze-natives" % "0.13.1",
  "org.scalanlp" %% "breeze-viz" % "0.13.1",
  "org.scala-sbt" % "io_2.11" % "1.0.0-M1",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
  "net.debasishg" %% "redisclient" % "3.4",
  "com.typesafe.akka" %% "akka-stream" % "2.5.6",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.6" % Test
)

lazy val core = (project in file("core"))
  .settings(
    commonSettings ++ Seq(
      libraryDependencies ++= dependencies
    )
  )

lazy val matrix_factorization = (project in file("matrix_factorization"))
  .dependsOn(core)
  .settings(
    commonSettings
  )

lazy val factorization_machines = (project in file("factorization_machines"))
  .dependsOn(core)
  .settings(
    commonSettings
  )
