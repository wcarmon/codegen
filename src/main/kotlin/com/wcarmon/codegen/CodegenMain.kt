@file:JvmName("CodegenMain")

package com.wcarmon.codegen

import com.wcarmon.codegen.config.CodegenBeans
import com.wcarmon.codegen.config.JSONBeans
import org.apache.logging.log4j.LogManager
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import java.nio.file.Paths
import kotlin.system.exitProcess

private val LOG = LogManager.getLogger("com.wcarmon.codegen.CodegenMain")


/**
 * Entry point
 *
 * Pass exactly 1-directory as command line arg.
 *
 * Directory must contain at-least-one entity json file
 * Directory must contain at-least-one generator request json file
 *
 * See [PATTERN_FOR_ENTITY_FILE]
 * See [PATTERN_FOR_GEN_REQ_FILE]
 */
fun main(args: Array<String>) {

  if (args.size != 1) {
    LOG.error("Pass exactly 1 directory as an argument")
    exitProcess(1)
  }

  val configRoot = Paths.get(args[0]).normalize().toAbsolutePath()
  LOG.info("Starting codegen:  configRoot={}", configRoot)

  val ctx = SpringApplicationBuilder(CodeGeneratorApp::class.java)
    .bannerMode(Banner.Mode.OFF)
    .headless(true)
    .logStartupInfo(true)
    .profiles("default")
    .web(WebApplicationType.NONE)
    .sources(
      JSONBeans::class.java,
      CodegenBeans::class.java,
    )
    .build()
    .run(*args)

  try {
    ctx.getBean(CodeGeneratorApp::class.java)
      .run(configRoot)

    ctx.close()
    exitProcess(0)

  } catch (ex: Exception) {
    LOG.error("Failed to run generator:", ex)
    SpringApplication.exit(ctx, { 0 })
    exitProcess(2)
  }
}
