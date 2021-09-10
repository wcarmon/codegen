package com.wcarmon.codegen.sandbox

import freemarker.template.Configuration
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration

private val LOG = LogManager.getLogger("NodeUtils")


fun buildNodeSandbox(
  nodeConfig: NodeConfig,
  freemarkerConfig: Configuration = getFreemarkerConfig(),

  /** Relative to sandbox root */
  directoriesToCreate: List<String> = RELATIVE_DIRS_FOR_NODE_PROJECT,

  /** Map<TemplateFileClasspath, PathRelativeToNewSandboxRoot> */
  templateMappings: Map<String, String> = NODE_TEMPLATE_TO_RELATIVE_OUTPUT_PATH_MAPPING,
) {

  LOG.info("Creating Node sandbox directory: path=${nodeConfig.cleanProjectRoot}")

  // -- Create dirs
  Files.createDirectories(
    nodeConfig.cleanProjectRoot)

  val rootAsString = nodeConfig.cleanProjectRoot.toString()

  directoriesToCreate
    .forEach { relativePath ->
      Files.createDirectories(
        Paths.get(rootAsString, relativePath))
    }


  // -- Generate files
  val dataForTemplate = mapOf(
    "nodeConfig" to nodeConfig,
  )

  templateMappings
    .forEach { (templatePath: String, relativeOutputPath: String) ->

      generateFileFromTemplate(
        dataForTemplate = dataForTemplate,
        dest = Paths.get(rootAsString, relativeOutputPath).normalize(),
        template = freemarkerConfig.getTemplate(templatePath),
      )
    }


  // -- install angular cli
  val command = listOf(
    nodeConfig.npmBinary.toString(),
    "install",
    "@angular/cli",
    "--loglevel=warn",
  )

  LOG.info("Installing angular cli...")
  val npmInstallOutput = executeCommand(
    command = command,
    maxWait = Duration.ofMinutes(1),
    rawWorkingDir = nodeConfig.cleanProjectRoot,
  )
  npmInstallOutput.printStdOut()
  npmInstallOutput.printStdErr()

  val ngNewOutput = ngNew(nodeConfig)
  ngNewOutput.printStdOut()
  ngNewOutput.printStdErr()
}

fun runNodeUnitTests(
  nodeConfig: NodeConfig,
  maxWait: Duration = Duration.ofMinutes(5),
): ProcessOutput {
  LOG.info("Running node unit tests...")

  TODO("finish runNodeUnitTests")
}

fun ngServe(
  nodeConfig: NodeConfig,
  maxWait: Duration = Duration.ofMinutes(5),
): ProcessOutput {
  LOG.info("Running `ng serve`...")

  TODO("ng serve --open")
}

/**
 * See https://angular.io/cli/new
 */
fun ngNew(
  nodeConfig: NodeConfig,
  maxWait: Duration = Duration.ofMinutes(1),
): ProcessOutput {
  LOG.info("Running `ng new`...")

  return executeCommand(
    command = listOf(
      nodeConfig.npxBinary.toString(),
      "ng",
      "new",
      nodeConfig.projectName,
      "--create-application",
      "--defaults",
      "--directory=${nodeConfig.cleanProjectRoot}",
      "--force",
      "--inline-template=false",
      "--interactive=false",
      "--minimal=false",
      "--package-manager=npm",
      "--routing=true",
      "--skip-install=false",
      "--skip-tests=false",
      "--strict=true",
      "--style=css",
      "--verbose=false",
      "--view-encapsulation=ShadowDom",
    ),
    maxWait = maxWait,
    rawWorkingDir = nodeConfig.cleanProjectRoot,
  )
}
