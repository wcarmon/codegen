package com.wcarmon.codegen.sandbox

import java.nio.file.Files
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom

fun main() {

  val gradleConfig =
    GradleConfig(
      fullyQualifiedMainClass = "SandboxMain",
      gradleBinary = DEFAULT_GRADLE_BINARY,
      gradleVersion = "7.2",
      includeProto = true,
      includeSQLDelight = true,
      projectGroup = "io.wc.codegen.sandbox",
      projectName = "sandbox-" + ThreadLocalRandom.current().nextInt(20),
      projectRoot = Files.createTempDirectory("codegen-sandbox-"),
      projectVersion = "1.0.0-SNAPSHOT",
    )

  buildGradleSandbox(gradleConfig)

  // -- Confirm the generated tests pass
  gradleTest(
    gradleConfig,
    maxWait = Duration.ofSeconds(20),
  )

  // -- Run the generated app
  val processOutput = gradleRun(
    gradleConfig,
    maxWait = Duration.ofSeconds(30),
  )

  println("`gradle run` output: $processOutput")
}
