ThisBuild / scalaVersion := "2.13.3"

val scalaJSDom = "1.0.0"
val scalaJSReact = "1.7.5"
val scalaCss = "0.6.1"
val reactJS = "16.13.1"

lazy val root = project
  .in(file("."))
  .aggregate(kafkafob.js, kafkafob.jvm)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val kafkafob = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .settings(
    name := "Kafkafob",
    version := "0.1"
  )
  .jsConfigure(_.enablePlugins(ScalaJSBundlerPlugin, JSDependenciesPlugin))
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaJSDom,
      "com.github.japgolly.scalajs-react" %%% "core" % scalaJSReact,
      "com.github.japgolly.scalajs-react" %%% "extra" % scalaJSReact,
      "com.github.japgolly.scalacss" %%% "core" % scalaCss,
      "com.github.japgolly.scalacss" %%% "ext-react" % scalaCss
    ),

    npmDependencies in Compile ++= Seq(
      "react" -> reactJS,
      "react-dom" -> reactJS),

    jsDependencies ++= Seq(
      "org.webjars.npm" % "react" % reactJS
        /        "umd/react.development.js"
        minified "umd/react.production.min.js"
        commonJSName "React",
      "org.webjars.npm" % "react-dom" % reactJS
        /         "umd/react-dom.development.js"
        minified  "umd/react-dom.production.min.js"
        dependsOn "umd/react.development.js"
        commonJSName "ReactDOM",
      "org.webjars.npm" % "react-dom" % reactJS
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
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % "2.6.0" withSources(),
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.2" withSources(),
      "org.slf4j" % "slf4j-simple" % "1.7.30" % Test withSources(),
      "com.typesafe" % "config" % "1.4.0" % Test withSources(),
      "com.lihaoyi" %% "utest" % "0.7.5" % Test withSources(),
      "org.scalacheck" %% "scalacheck" % "1.14.3" % Test withSources(),
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )




