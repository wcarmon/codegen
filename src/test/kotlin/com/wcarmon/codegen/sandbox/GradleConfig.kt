package com.wcarmon.codegen.sandbox

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolute

data class GradleConfig(
  val projectName: String,

  val fullyQualifiedMainClass: String = "SandboxMain",
  val gradleBinary: Path = DEFAULT_GRADLE_BINARY,
  val gradleVersion: String = "7.2",
  val includeProto: Boolean = true,
  val includeSQLDelight: Boolean = true,
  val projectGroup: String = "io.wc.codegen.sandbox",
  val projectRoot: Path = Files.createTempDirectory("codegen-sandbox-"),
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
