package com.wcarmon.codegen.sandbox

import freemarker.template.Configuration
import freemarker.template.Template
import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolute


private val LOG = LogManager.getLogger("GradleUtils");

val DEFAULT_GRADLE_CONFIG = GradleConfig(
  projectName = "sandbox-001",
)

/**
 * @return normalized root path for new project
 */
fun buildGradleSandbox(
  freemarkerConfig: Configuration = getFreemarkerConfig(),
  gradleConfig: GradleConfig = DEFAULT_GRADLE_CONFIG,
  rootDir: Path = Files.createTempDirectory("codegen-sandbox-"),
): Path {

  val cleanRootDir = rootDir.normalize().absolute()

  LOG.info("Creating sandbox directory: path=$cleanRootDir")
  Files.createDirectories(cleanRootDir)

  RELATIVE_PATHS_FOR_GRADLE_PROJECT
    .forEach { relativePath ->
      Files.createDirectories(
        Paths.get(cleanRootDir.toString(), relativePath))
    }

  val dataForTemplate = mapOf(
    "gradleConfig" to gradleConfig
  )


  // -- Generate files
  TEMPLATE_TO_RELATIVE_OUTPUT_PATH_MAPPING
    .forEach { templatePath: String, relativeOutputPath: String ->

      createFileFromTemplate(
        dataForTemplate = dataForTemplate,
        dest = Paths.get(cleanRootDir.toString(), relativeOutputPath).normalize(),
        template = freemarkerConfig.getTemplate(templatePath),
      )
    }

  // -- protobuf.gradle
  copyProtoGradleFile(cleanRootDir)


  //TODO: build.gradle.kts template contents
  //TODO: gradle wrapper
  //TODO: gradle init

  return cleanRootDir
}

private fun createFileFromTemplate(
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

private fun copyProtoGradleFile(
  rootDir: Path,
) {
  val protoGradleDest = Paths.get(rootDir.toString(), "protobuf.gradle")

  Files.copy(
    GradleConfig::class.java.getResourceAsStream(TemplatePaths.GRADLE_PROTO_FILE)!!,
    protoGradleDest,
  )

  LOG.info("Wrote file: path=$protoGradleDest")
}
