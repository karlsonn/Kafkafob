ThisBuild / scalaVersion := "2.13.3"

lazy val scalaJSDomVersion = "1.0.0"
lazy val scalaJSReactVersion = "1.7.5"
lazy val scalaCssVersion = "0.6.1"
lazy val reactJSVersion = "16.13.1"

lazy val akkaVersion = "2.6.9"
lazy val akkaHttpVersion = "10.2.0"
lazy val akkaGrpcVersion = "1.0.2"

/*
lazy val root = project
  .in(file("."))
  .aggregate(kafkafob.js, kafkafob.jvm)
  .settings(
    publish := {},
    publishLocal := {}
  )
*/

lazy val kafkafob = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .settings(
    name := "Kafkafob",
    version := "0.1"
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin, JSDependenciesPlugin))
  .jsSettings(
    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaJSDomVersion,
      "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReactVersion,
      "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReactVersion,
      "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
      "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion,
      "com.lihaoyi" %%% "utest" % "0.7.5" % Test withSources(),
    ),

    npmDependencies in Compile ++= Seq(
      "react" -> reactJSVersion,
      "react-dom" -> reactJSVersion),

    jsDependencies ++= Seq(
      "org.webjars.npm" % "react" % reactJSVersion
        /        "umd/react.development.js"
        minified "umd/react.production.min.js"
        commonJSName "React",
      "org.webjars.npm" % "react-dom" % reactJSVersion
        /         "umd/react-dom.development.js"
        minified  "umd/react-dom.production.min.js"
        dependsOn "umd/react.development.js"
        commonJSName "ReactDOM",
      "org.webjars.npm" % "react-dom" % reactJSVersion
        /         "umd/react-dom-server.browser.development.js"
        minified  "umd/react-dom-server.browser.production.min.js"
        dependsOn "umd/react-dom.development.js"
        commonJSName "ReactDOMServer"),

    //skip in packageJSDependencies := false,

    scalaJSUseMainModuleInitializer := true,

    //crossTarget in (Compile, fullOptJS) := file("js"),
    //crossTarget in (Compile, fastOptJS) := file("js"),
    //crossTarget in (Compile, packageJSDependencies) := file("js"),
    //crossTarget in (Compile, packageMinifiedJSDependencies) := file("js"),
    //artifactPath in (Compile, fastOptJS) := ((crossTarget in (Compile, fastOptJS)).value / ((moduleName in fastOptJS).value + ".js")),

    scalacOptions += "-feature",

    dependencyOverrides ++= Seq(
      "org.webjars.npm" % "js-tokens" % "4.0.0",
      "org.webjars.npm" % "scheduler" % "0.14.0"
    )
  )
  .jvmConfigure(_.enablePlugins(AkkaGrpcPlugin))
  .jvmSettings(
    testFrameworks += new TestFramework("utest.runner.Framework"),

    //PB.targets in Compile := Seq(scalapb.gen() -> (sourceManaged in Compile).value / "scalapb"),

    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % "2.6.0" withSources(),
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.2" withSources(),

      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-pki" % akkaVersion,
      // The Akka HTTP overwrites are required because Akka-gRPC depends on 10.1.x
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http2-support" % akkaHttpVersion,

      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,

      //"io.grpc" % "grpc-netty" % scalapb.compiler.Version.grpcJavaVersion,
      //"com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
      //"com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf", //if we will use default .proto

      "org.scalatest" %% "scalatest" % "3.1.1" % Test withSources(),
      "org.slf4j" % "slf4j-simple" % "1.7.30" % Test withSources(),
      "com.typesafe" % "config" % "1.4.0" % Test withSources(),
      "com.lihaoyi" %% "utest" % "0.7.5" % Test withSources(),
      "org.scalacheck" %% "scalacheck" % "1.14.3" % Test withSources(),
    ),
  )