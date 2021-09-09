package com.wcarmon.codegen.sandbox

import freemarker.template.Configuration
import org.apache.logging.log4j.LogManager
import java.time.Duration

private val LOG = LogManager.getLogger("NodeUtils")


fun buildNodeSandbox(
  nodeConfig: NodeConfig,
  freemarkerConfig: Configuration = getFreemarkerConfig(),
) {
  LOG.info("Creating Node sandbox directory: path=${nodeConfig.cleanProjectRoot}")


  // -- install angular cli
  val command = listOf(
    nodeConfig.npmBinary.toString(),
    "install",
    "@angular/cli",
  )

  val npmInstallOutput = executeCommand(
    command = command,
    maxWait = Duration.ofMinutes(1),
    rawWorkingDir = nodeConfig.cleanProjectRoot,
  )

  //TODO: more here
}
