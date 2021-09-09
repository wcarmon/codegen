package com.wcarmon.codegen.sandbox

import freemarker.template.Configuration
import freemarker.template.Template
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration


private val LOG = LogManager.getLogger("GradleUtils")


/**
 * Execute `gradle run`
 */
fun gradleRun(
  gradleConfig: GradleConfig,
  maxWait: Duration = Duration.ofMinutes(5),
): ProcessOutput {
  require(!maxWait.isNegative)
  require(Files.exists(gradleConfig.cleanProjectRoot))

  LOG.info("Executing: `gradle run` ...")
  return executeCommand(
    command = listOf(
      "./gradlew",
      "build",
      "--exclude-task=distZip",
      "--exclude-task=shadowJar",
      "--exclude-task=test",
      "run",  //TODO: is "bootRun" better?
      "--quiet",
    ),
    maxWait = maxWait,
    rawWorkingDir = gradleConfig.cleanProjectRoot,
  )
}

/**
 * Execute `gradle test`
 */
fun gradleTest(
  gradleConfig: GradleConfig,
  maxWait: Duration = Duration.ofMinutes(5),
): ProcessOutput {
  require(!maxWait.isNegative)
  require(Files.exists(gradleConfig.cleanProjectRoot))

  LOG.info("Executing: `gradle test` ...")
  return executeCommand(
    command = listOf(
      "./gradlew",
      "clean",
      "test",
      "--exclude-task=distZip",
      "--exclude-task=shadowJar",
      "--quiet",
    ),
    maxWait = maxWait,
    rawWorkingDir = gradleConfig.cleanProjectRoot,
  )
}

/**
 * @return normalized root path for new project
 */
fun buildGradleSandbox(
  gradleConfig: GradleConfig,
  freemarkerConfig: Configuration = getFreemarkerConfig(),
) {

  LOG.info("Creating sandbox directory: path=${gradleConfig.cleanProjectRoot}")
  Files.createDirectories(gradleConfig.cleanProjectRoot)

  RELATIVE_PATHS_FOR_GRADLE_PROJECT
    .forEach { relativePath ->
      Files.createDirectories(
        Paths.get(gradleConfig.cleanProjectRoot.toString(), relativePath))
    }

  val dataForTemplate = mapOf(
    "gradleConfig" to gradleConfig
  )

  // -- Generate files
  TEMPLATE_TO_RELATIVE_OUTPUT_PATH_MAPPING
    .forEach { (templatePath: String, relativeOutputPath: String) ->

      generateFileFromTemplate(
        dataForTemplate = dataForTemplate,
        dest = Paths.get(gradleConfig.cleanProjectRoot.toString(), relativeOutputPath).normalize(),
        template = freemarkerConfig.getTemplate(templatePath),
      )
    }

  // -- protobuf.gradle
  copyProtoGradleFile(gradleConfig.cleanProjectRoot)

  addGradleWrapper(
    gradleConfig = gradleConfig,
  )
}

/**
 * Execute `gradle wrapper`
 */
private fun addGradleWrapper(
  gradleConfig: GradleConfig,
  maxWait: Duration = Duration.ofSeconds(30),
) {
  check(Files.exists(gradleConfig.cleanProjectRoot))
  check(Files.isDirectory(gradleConfig.cleanProjectRoot))

  executeCommand(
    command = listOf(
      gradleConfig.gradleBinary.toString(),
      "wrapper",
      "--distribution-type=all",
      "--gradle-version=${gradleConfig.gradleVersion}",
      "--quiet",
    ),
    maxWait = maxWait,
    rawWorkingDir = gradleConfig.cleanProjectRoot,
  )

  LOG.info("Added gradle wrapper to ${gradleConfig.cleanProjectRoot}")
}

private fun generateFileFromTemplate(
  dataForTemplate: Map<String, Any>,
  dest: Path,
  template: Template,
) {
  check(!Files.exists(dest)) {
    "Failure: file already exists at $dest"
  }

  Files.newBufferedWriter(dest)
    .use { writer ->
      template.process(dataForTemplate, writer)
    }

  LOG.info("Wrote file: path=$dest")
}

//Only required because I cannot figure out
// how to convert the plugins section of protobuf.gradle to *.kts format
private fun copyProtoGradleFile(
  rootDir: Path,
) {
  val dest = Paths.get(rootDir.toString(), "protobuf.gradle")
  check(!Files.exists(dest))

  Files.copy(
    GradleConfig::class.java.getResourceAsStream(TemplatePaths.GRADLE_PROTO_FILE)!!,
    dest,
  )

  LOG.info("Wrote file: path=$dest")
}
