package com.wcarmon.codegen.sandbox

import freemarker.template.Configuration
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

fun buildGradleSandbox(
  gradleConfig: GradleConfig,
  freemarkerConfig: Configuration = getFreemarkerConfig(),

  /** Relative to sandbox root */
  directoriesToCreate: List<String> = RELATIVE_DIRS_FOR_GRADLE_PROJECT,

  /** Map<TemplateFileClasspath, PathRelativeToNewSandboxRoot> */
  templateMappings: Map<String, String> = GRADLE_TEMPLATE_TO_RELATIVE_OUTPUT_PATH_MAPPING,
) {

  LOG.info("Creating Gradle sandbox directory: path=${gradleConfig.cleanProjectRoot}")

  // -- Create dirs
  Files.createDirectories(
    gradleConfig.cleanProjectRoot)

  val rootAsString = gradleConfig.cleanProjectRoot.toString()

  directoriesToCreate
    .forEach { relativePath ->
      Files.createDirectories(
        Paths.get(rootAsString, relativePath))
    }


  // -- Generate files
  val dataForTemplate = mapOf(
    "gradleConfig" to gradleConfig,
  )

  templateMappings
    .forEach { (templatePath: String, relativeOutputPath: String) ->

      generateFileFromTemplate(
        dataForTemplate = dataForTemplate,
        dest = Paths.get(rootAsString, relativeOutputPath).normalize(),
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


//Only required because I cannot figure out
// how to convert the plugins section of protobuf.gradle to *.kts format
private fun copyProtoGradleFile(
  rootDir: Path,
) {
  val dest = Paths.get(rootDir.toString(), "protobuf.gradle")
  check(!Files.exists(dest))

  Files.copy(
    GradleConfig::class.java.getResourceAsStream(
      TemplatePaths.GRADLE_PROTO_FILE)!!,
    dest,
  )

  LOG.info("Wrote file: path=$dest")
}
