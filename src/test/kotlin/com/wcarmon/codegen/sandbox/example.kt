package com.wcarmon.codegen.sandbox

import java.nio.file.Files
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom

fun main() {

  Files.createDirectories(CODEGEN_TEMP_DIR)

  val sandboxConfig = SandboxConfig.fromProperties("/sandbox.properties")

  val gradleConfig = sandboxConfig.gradleConfig
    .copy(
      projectName = "sandbox-${ThreadLocalRandom.current().nextInt(20)}",
      projectRoot = Files.createTempDirectory(CODEGEN_TEMP_DIR, "gradle-sandbox-"),
    )

  val nodeConfig = sandboxConfig.nodeConfig
    .copy(
      //GOTCHA: node doesn't allow hyphens in project name
      projectName = "sandbox${ThreadLocalRandom.current().nextInt(20)}",
      projectRoot = Files.createTempDirectory(CODEGEN_TEMP_DIR, "node-sandbox-"),
    )

  doNodeStuff(nodeConfig)
//  doGradleStuff(gradleConfig)
}

fun doNodeStuff(nodeConfig: NodeConfig) {
  buildNodeSandbox(nodeConfig)

  runNodeUnitTests(nodeConfig)

  ngServe(nodeConfig)
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
