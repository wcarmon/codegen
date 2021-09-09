package com.wcarmon.codegen.sandbox

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolute

/**
 * Properties required to generate a gradle project
 */
data class GradleConfig(
  val projectName: String,
  val gradleBinary: Path,

  val fullyQualifiedMainClass: String = "SandboxMain",
  val gradleVersion: String = "7.2",
  val includeProto: Boolean = true,
  val includeSQLDelight: Boolean = true,
  val projectGroup: String = "io.wc.codegen.sandbox",
  val projectRoot: Path = Files.createTempDirectory("codegen-gradle-sandbox-"),
  val projectVersion: String = "1.0.0-SNAPSHOT",
) {

  init {
    if (Files.exists(projectRoot)) {
      require(Files.isDirectory(projectRoot))
    }

    require(gradleVersion.isNotBlank()) { "gradleVersion is required" }
    require(projectGroup.isNotBlank()) { "projectGroup is required" }
    require(projectName.isNotBlank()) { "projectName is required" }
    require(projectVersion.isNotBlank()) { "projectVersion is required" }
  }

  val cleanProjectRoot = projectRoot.normalize().absolute()
}
