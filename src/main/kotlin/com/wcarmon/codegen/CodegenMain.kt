@file:JvmName("CodegenMain")

package com.wcarmon.codegen

import com.wcarmon.codegen.config.CodegenBeans
import com.wcarmon.codegen.config.FreemarkerBeans
import com.wcarmon.codegen.config.JSONBeans
import com.wcarmon.codegen.log.structuredError
import com.wcarmon.codegen.log.structuredInfo
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
 * Pass exactly 1-directory, as command line arg.
 *
 * Directory must contain at-least-1 entity json file
 * See [PATTERN_FOR_ENTITY_FILE]
 *
 * Directory must contain at-least-1 code generate request json file
 * See [PATTERN_FOR_GEN_REQ_FILE]
 */
fun main(args: Array<String>) {

  if (args.size != 1) {
    val exitCode = 1
    LOG.structuredError(
      "Pass exactly 1 argument",
      "args" to args,
      "details" to "Pass directory containing request json files",
      "exitCode" to exitCode,
      "nextStep" to "terminating",
    )

    exitProcess(exitCode)
  }

  val configRoot = Paths.get(args[0]).normalize().toAbsolutePath()
  LOG.structuredInfo(
    "Starting codegen",
    "configRoot" to configRoot,
  )

  val ctx = SpringApplicationBuilder(CodeGeneratorApp::class.java)
    .bannerMode(Banner.Mode.OFF)
    .headless(true)
    .logStartupInfo(true)
    .profiles("default")
    .web(WebApplicationType.NONE)
    .sources(
      CodegenBeans::class.java,
      FreemarkerBeans::class.java,
      JSONBeans::class.java,
    )
    .build()
    .run(*args)

  try {
    ctx.getBean(CodeGeneratorApp::class.java)
      .run(configRoot)

    LOG.info("Success")

  } catch (ex: Exception) {
    val exitCode = 2
    LOG.structuredError(
      "Failed to run generator",
      "args" to args,
      "configRoot" to configRoot,
      "exception" to ex,
    )

    SpringApplication.exit(ctx, { 0 })
    exitProcess(exitCode)

  } finally {
    ctx.close()
  }

  exitProcess(0)
}
