package com.wcarmon.codegen.sandbox

import freemarker.template.Configuration
import org.apache.logging.log4j.LogManager

private val LOG = LogManager.getLogger("NodeUtils")


fun buildNodeSandbox(
  nodeConfig: NodeConfig,
  freemarkerConfig: Configuration = getFreemarkerConfig(),
) {
  LOG.info("Creating Node sandbox directory: path=${nodeConfig.cleanProjectRoot}")

}
