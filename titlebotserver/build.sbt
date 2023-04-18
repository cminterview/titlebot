lazy val akkaHttpVersion = "10.5.0"
lazy val akkaVersion = "2.8.0"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "com.eck",
      scalaVersion := "2.13.4"
    )),
    name := "aTitleBot",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.11",
      "net.ruippeixotog" %% "scala-scraper" % "3.0.0",
      "org.mongodb.scala" %% "mongo-scala-driver" % "4.9.0",
      "ch.megard" %% "akka-http-cors" % "1.2.0"

    )
  )
