package com.wcarmon.codegen.sandbox

import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.io.path.absolute

data class NodeConfig(
  val nodeBinary: Path,
  val npmBinary: Path,
  val npxBinary: Path,
  val projectName: String = "codegen-sandbox",
  val projectRoot: Path = Files.createTempDirectory("codegen-node-sandbox-"),
  val projectVersion: String = "0.0.1",

//TODO: add things from package json
) {

  companion object {

    @JvmStatic
    fun parseProperties(p: Properties) = NodeConfig(
      nodeBinary = p.parseRequiredPath("node.binary"),
      npmBinary = p.parseRequiredPath("npm.binary"),
      npxBinary = p.parseRequiredPath("npx.binary"),
      projectName = p.parseRequiredString("node.project.name"),
      projectRoot = p.parseRequiredPath("node.project.root"),
      projectVersion = p.parseRequiredString("node.project.version"),
    )
  }

  init {
    if (Files.exists(projectRoot)) {
      require(Files.isDirectory(projectRoot))
    }

    require(Files.exists(nodeBinary)) { "Cannot find node binary at $nodeBinary" }
    require(Files.exists(npmBinary)) { "Cannot find npm binary at $npmBinary" }
    require(Files.exists(npxBinary)) { "Cannot find npx binary at $npxBinary" }

    require(projectVersion.isNotBlank()) { "projectVersion is required" }
    require(projectName.isNotBlank()) { "projectName is required" }
  }


  val cleanProjectRoot = projectRoot.normalize().absolute()
}
