object ScalacOptions {
  val flags = Seq(
    "-encoding", "utf-8",    // Specify character encoding used by source files.
    "-unchecked",            // Enable additional warnings where generated code depends on assumptions.
    "-deprecation",          // Emit warning and location for usages of deprecated APIs.
    "-feature"               // Emit warning and location for usages of features that should be imported explicitly.
    //"-Xfatal-warnings"
  )
  val jsFlags = flags ++ Seq(
    "-Ymacro-annotations",
    "-explaintypes"          // Explain type errors in more detail.
  )
  val jvmFlags = flags ++ Seq(
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-language:postfixOps"
  )
}
