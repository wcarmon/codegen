package com.wcarmon.codegen.sandbox

import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolute

fun loadConfigFromProperties(
  classpath: String = "/sandbox.properties",
): SandboxConfig {
  require(classpath.isNotBlank())

  SandboxConfig::class.java
    .getResourceAsStream(classpath)
    .use { inStream ->
      checkNotNull(inStream) { "Failed to load properties.  classpath=$classpath" }

      val props = Properties().also {
        it.load(inStream)
      }

      return parseProperties(props)
    }
}


fun parseProperties(
  p: Properties,
) =
  SandboxConfig(
    gradleConfig = GradleConfig(
      fullyQualifiedMainClass = p.parseRequiredString("gradle.main-class.fully-qualified"),
      gradleBinary = p.parseRequiredPath("gradle.binary"),
      gradleVersion = p.parseRequiredString("gradle.version"),
      includeProto = p.parseOptionalBoolean("gradle.include.proto", true),
      includeSQLDelight = p.parseOptionalBoolean("gradle.include.sqldelight", true),
      projectGroup = p.parseRequiredString("gradle.project.group"),
      projectName = p.parseRequiredString("gradle.project.name"),
      projectRoot = Paths.get(p.getProperty("gradle.project.root").trim()).normalize().absolute(),
      projectVersion = p.getProperty("gradle.project.version").trim(),
    ),
    nodeConfig = NodeConfig(
      nodeBinary = Paths.get(p.getProperty("node.binary").trim()),
      npmBinary = Paths.get(p.getProperty("npm.binary").trim()),
      npxBinary = Paths.get(p.getProperty("npx.binary").trim()),
      projectVersion = p.getProperty("node.project.version").trim(),
    ),
  )

private fun Properties.parseOptionalString(
  propertyName: String,
  defaultIfBlank: String,
): String {
  val value = getProperty(propertyName, "").trim()

  if (value.isNotBlank()) {
    return defaultIfBlank
  }

  return value
}

private fun Properties.parseRequiredString(
  propertyName: String,
): String {
  val value = getProperty(propertyName, "").trim()
  require(value.isNotBlank()) { "missing required property: $propertyName" }

  return value
}

private fun Properties.parseRequiredPath(
  propertyName: String,
): Path {
  val value = getProperty(propertyName, "").trim()
  require(value.isNotBlank()) { "missing required path property: $propertyName" }

  return Paths.get(value)
}

private fun Properties.parseRequiredBoolean(
  propertyName: String,
): Boolean {

  val value = getProperty(propertyName, "").trim().lowercase()

  check(value == "true" || value == "false") {
    "invalid boolean property: $propertyName"
  }

  return value == "true"
}

private fun Properties.parseOptionalBoolean(
  propertyName: String,
  defaultIfMissing: Boolean = false,
): Boolean {
  val value = getProperty(propertyName, "").trim().lowercase()

  if (value.isNotBlank()) {
    check(value == "true" || value == "false") {
      "invalid boolean property: $propertyName"
    }

    return value == "true"
  }

  return defaultIfMissing
}
