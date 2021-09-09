package com.wcarmon.codegen.sandbox

import java.nio.file.Files
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom

fun main() {

  val parsedConfig = loadConfigFromProperties("/sandbox.properties")

  val gradleConfig = parsedConfig.gradleConfig
    .copy(
      projectName = "sandbox-" + ThreadLocalRandom.current().nextInt(20),
      projectRoot = Files.createTempDirectory("codegen-gradle-sandbox-"),
    )

  val nodeConfig = parsedConfig.nodeConfig

  doNodeStuff(nodeConfig)
  doGradleStuff(gradleConfig)
}

fun doNodeStuff(nodeConfig: NodeConfig) {
  buildNodeSandbox(nodeConfig)

  //TODO: run tests
  //TODO: run app (local webserver?)

  TODO("Not yet implemented")
}

fun doGradleStuff(gradleConfig: GradleConfig) {
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
