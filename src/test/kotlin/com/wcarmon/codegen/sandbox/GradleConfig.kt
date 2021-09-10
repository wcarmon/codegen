package com.wcarmon.codegen.sandbox

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
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

  companion object {

    @JvmStatic
    fun parseProperties(p: Properties) = GradleConfig(
      fullyQualifiedMainClass = p.parseRequiredString("gradle.main-class.fully-qualified"),
      gradleBinary = p.parseRequiredPath("gradle.binary"),
      gradleVersion = p.parseRequiredString("gradle.version"),
      includeProto = p.parseOptionalBoolean("gradle.include.proto", true),
      includeSQLDelight = p.parseOptionalBoolean("gradle.include.sqldelight", true),
      projectGroup = p.parseRequiredString("gradle.project.group"),
      projectName = p.parseRequiredString("gradle.project.name"),
      projectRoot = p.parseRequiredPath("gradle.project.root"),
      projectVersion = p.parseRequiredString("gradle.project.version"),
    )
  }

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
