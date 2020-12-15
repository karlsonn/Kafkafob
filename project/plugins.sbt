resolvers += Resolver.bintrayRepo("oyvindberg", "converter")

addSbtPlugin("org.scala-js"                % "sbt-scalajs"                % "1.3.1")
addSbtPlugin("org.portable-scala"          % "sbt-scalajs-crossproject"   % "1.0.0")
addSbtPlugin("com.lightbend.akka.grpc"     % "sbt-akka-grpc"              % "1.0.2")
addSbtPlugin("com.lightbend.sbt"           % "sbt-javaagent"              % "0.1.5")
addSbtPlugin("org.scalablytyped.converter" % "sbt-converter"              % "1.0.0-beta29.1")
addSbtPlugin("org.scala-js"                % "sbt-jsdependencies"         % "1.0.2")  //update it
addSbtPlugin("ch.epfl.scala"               % "sbt-scalajs-bundler"        % "0.18.0") //update it
addSbtPlugin("com.typesafe.sbt"            % "sbt-native-packager"        % "1.7.5")  //update it

// For Scala.js 1.x
//libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "1.1.0"