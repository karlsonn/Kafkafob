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
  )*/

import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING

lazy val start = TaskKey[Unit]("start")
lazy val dist = TaskKey[File]("dist")

lazy val kafkafob = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .settings(
    name := "Kafkafob",
    version := "0.1"
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin, JSDependenciesPlugin, ScalaJSPlugin, ScalablyTypedConverterPlugin))
  .jsSettings(
    //testFrameworks += new TestFramework("utest.runner.Framework"),
    PB.targets in Compile := Seq(
      //scalapb.gen() -> (sourceManaged in Compile).value,
      scalapb.gen(grpc=false) -> (sourceManaged in Compile).value,
      scalapb.grpcweb.GrpcWebCodeGenerator -> (sourceManaged in Compile).value
    ),

    Compile / npmDependencies += "react" -> "16.13.1",
    Compile / npmDependencies += "react-dom" -> "16.13.1",
    Compile / npmDependencies += "@types/react" -> "16.9.42",
    Compile / npmDependencies += "@types/react-dom" -> "16.9.8",
    Compile / npmDependencies += "csstype" -> "2.6.11",
    Compile / npmDependencies += "@types/prop-types" -> "15.7.3",
    Compile / npmDependencies += "antd" -> "4.5.1",
    Compile / npmDependencies += "grpc-web" -> "1.2.1",

    stTypescriptVersion := "3.9.3",

    Compile / npmDevDependencies += "file-loader" -> "6.0.0",
    Compile / npmDevDependencies += "style-loader" -> "1.2.1",
    Compile / npmDevDependencies += "css-loader" -> "3.5.3",
    Compile / npmDevDependencies += "html-webpack-plugin" -> "4.3.0",
    Compile / npmDevDependencies += "copy-webpack-plugin" -> "5.1.1",
    Compile / npmDevDependencies += "webpack-merge" -> "4.2.2",
    Compile / npmDevDependencies += "url-loader" -> "3.0.0",

    Compile / fullOptJS / webpackDevServerExtraArgs += "--mode=production",
    Compile / fastOptJS / webpackDevServerExtraArgs += "--mode=development",

    libraryDependencies += "me.shadaj" %%% "slinky-web" % "0.6.6",
    libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.6.6",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.1" % Test,
    libraryDependencies += "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion, //"0.10.8"
    libraryDependencies += "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
    libraryDependencies += "com.thesamet.scalapb.grpcweb" %%% "scalapb-grpcweb" % scalapb.grpcweb.BuildInfo.version,

    scalacOptions += "-Ymacro-annotations",

    webpackConfigFile := Some(baseDirectory.value / "webpack" / "custom.webpack.config.js"),

    useYarn := true,
    webpackDevServerPort := 8080,
    stFlavour := Flavour.Slinky,

    scalacOptions ++= ScalacOptions.flags,
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(true),

    start := {
      (Compile / fastOptJS / startWebpackDevServer).value
    },

    dist := {
      val artifacts = (Compile / fullOptJS / webpack).value
      val artifactFolder = (Compile / fullOptJS / crossTarget).value
      val distFolder = (ThisBuild / baseDirectory).value / "docs" / moduleName.value

      distFolder.mkdirs()
      artifacts.foreach { artifact =>
        val target = artifact.data.relativeTo(artifactFolder) match {
          case None          => distFolder / artifact.data.name
          case Some(relFile) => distFolder / relFile.toString
        }

        Files.copy(artifact.data.toPath, target.toPath, REPLACE_EXISTING)
      }

      val indexFrom = baseDirectory.value / "src/main/js/index.html"
      val indexTo = distFolder / "index.html"

      val indexPatchedContent = {
        import collection.JavaConverters._
        Files
          .readAllLines(indexFrom.toPath, IO.utf8)
          .asScala
          .map(_.replaceAllLiterally("-fastopt-", "-opt-"))
          .mkString("\n")
      }

      Files.write(indexTo.toPath, indexPatchedContent.getBytes(IO.utf8))
      distFolder
    }

/*    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaJSDomVersion,
      "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReactVersion,
      "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReactVersion,
      "com.github.japgolly.scalacss" %%% "core" % scalaCssVersion,
      "com.github.japgolly.scalacss" %%% "ext-react" % scalaCssVersion,

      "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion, //"0.10.8"
      "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
      "com.thesamet.scalapb.grpcweb" %%% "scalapb-grpcweb" % scalapb.grpcweb.BuildInfo.version,

      "com.lihaoyi" %%% "scalatags" % "0.9.2",
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
    )*/
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
      "ch.megard" %% "akka-http-cors" % "1.1.0",

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

    scalacOptions ++= Seq(
      "-encoding", "utf8", // Option and arguments on same line
      //"-Xfatal-warnings",  // New lines for each options
      //"-deprecation",
      "-unchecked",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    )
  )