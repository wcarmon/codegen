package com.wcarmon.codegen.sandbox

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
      fullyQualifiedMainClass = p.getProperty("gradle.main-class.fully-qualified").trim(),
      gradleBinary = Paths.get(p.getProperty("gradle.binary").trim()).normalize().absolute(),
      gradleVersion = p.getProperty("gradle.version").trim(),
      includeProto = p.getProperty("gradle.include.proto").trim().lowercase() == "true",
      includeSQLDelight = p.getProperty("gradle.include.sqldelight").trim().lowercase() == "true",
      projectGroup = p.getProperty("gradle.project.group").trim(),
      projectName = p.getProperty("gradle.project.name").trim(),
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
