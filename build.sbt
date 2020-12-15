ThisBuild / scalaVersion := "2.13.4"

lazy val scalaJSDomVersion = "1.0.0"
lazy val scalaJSReactVersion = "1.7.5"
lazy val scalaCssVersion = "0.6.1"
lazy val reactJSVersion = "16.13.1"

lazy val akkaVersion = "2.6.9"
lazy val akkaHttpVersion = "10.2.0"
lazy val akkaGrpcVersion = "1.0.2"

import java.nio.file.Files
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import collection.JavaConverters._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}
import scalapb.compiler.Version.scalapbVersion

lazy val start = TaskKey[Unit]("start")
lazy val dist = TaskKey[File]("dist")

lazy val kafkafobJS = kafkafob.js
lazy val kafkafobJVM = kafkafob.jvm

lazy val kafkafob = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .in(file("."))
  .settings(
    name := "kafkafob",
    version := "0.1",
    PB.protoSources in Compile := Seq((baseDirectory in ThisBuild).value / "shared" / "src"/ "main" / "protobuf"),
    libraryDependencies += "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapbVersion,
    libraryDependencies += "com.thesamet.scalapb" %%% "scalapb-runtime" % scalapbVersion % "protobuf",
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin, JSDependenciesPlugin, ScalaJSPlugin, ScalablyTypedConverterPlugin))
  .jsSettings(
    testFrameworks += new TestFramework("utest.runner.Framework"),
    PB.targets in Compile := Seq(
      scalapb.gen() -> (sourceManaged in Compile).value,
      scalapb.grpcweb.GrpcWebCodeGenerator -> (sourceManaged in Compile).value
    ),
    stTypescriptVersion := "3.9.3",

    Compile / npmDependencies += "react" -> "16.13.1",
    Compile / npmDependencies += "react-dom" -> "16.13.1",
    Compile / npmDependencies += "@types/react" -> "16.9.42",
    Compile / npmDependencies += "@types/react-dom" -> "16.9.8",
    Compile / npmDependencies += "csstype" -> "2.6.11",
    Compile / npmDependencies += "@types/prop-types" -> "15.7.3",
    Compile / npmDependencies += "antd" -> "4.5.1",
    Compile / npmDependencies += "grpc-web" -> "1.0.7",

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

    libraryDependencies += "com.thesamet.scalapb.grpcweb" %%% "scalapb-grpcweb" % scalapb.grpcweb.BuildInfo.version,

    libraryDependencies += "org.akka-js" %%% "akkajsactor" % "2.2.6.9",
    libraryDependencies += "org.akka-js" %%% "akkajsactorstream" % "2.2.6.9",
    libraryDependencies += "org.akka-js" %%% "akkajsactortyped" % "2.2.6.9",
    libraryDependencies += "org.akka-js" %%% "akkajsactorstreamtyped" % "2.2.6.9",
    libraryDependencies += "org.akka-js" %%% "akkajstestkit" % "2.2.6.9" % "test",
    libraryDependencies += "org.akka-js" %%% "akkajsstreamtestkit" % "2.2.6.9" % "test",
    libraryDependencies += "org.akka-js" %%% "akkajstypedtestkit" % "2.2.6.9" % "test",

    libraryDependencies += "com.github.julien-truffaut" %%%  "monocle-core"  % "2.0.4",
    libraryDependencies += "com.github.julien-truffaut" %%%  "monocle-macro" % "2.0.4",

    libraryDependencies += "com.github.julien-truffaut" %%%  "monocle-law"   % "2.0.4" % "test",

    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.1" % Test,
    libraryDependencies += "com.lihaoyi"   %%% "utest" % "0.7.5" % Test withSources(),

    webpackConfigFile := Some(baseDirectory.value / "webpack" / "custom.webpack.config.js"),

    scalaJSUseMainModuleInitializer := true,

    useYarn := true,
    webpackDevServerPort := 8080,
    stFlavour := Flavour.Slinky,

    scalacOptions ++= ScalacOptions.jsFlags,
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
      val indexPatchedContent = Files.readAllLines(indexFrom.toPath, IO.utf8).asScala.map(_.replaceAllLiterally("-fastopt-", "-opt-")).mkString("\n")

      Files.write(indexTo.toPath, indexPatchedContent.getBytes(IO.utf8))
      distFolder
    }
  )
  .jvmConfigure(_.enablePlugins(AkkaGrpcPlugin))
  .jvmSettings(
    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies ++= Seq(
      "org.apache.kafka"  % "kafka-clients"             % "2.6.0" withSources(),
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.2" withSources(),

      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka" %% "akka-discovery"           % akkaVersion,
      "com.typesafe.akka" %% "akka-pki"                 % akkaVersion,

      // The Akka HTTP overwrites are required because Akka-gRPC depends on 10.1.x
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http2-support"       % akkaHttpVersion,
      "ch.megard"         %% "akka-http-cors"           % "1.1.0",

      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit"      % akkaVersion % Test,

      "org.slf4j"         %  "slf4j-simple"             % "1.7.30" % Test withSources(),
      "com.typesafe"      %  "config"                   % "1.4.0" % Test withSources(),
      "com.lihaoyi"       %% "utest"                    % "0.7.5" % Test withSources(),
    ),
    scalacOptions ++= ScalacOptions.jvmFlags
  )