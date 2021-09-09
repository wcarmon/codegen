package com.wcarmon.codegen.sandbox

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolute

data class NodeConfig(
  val nodeBinary: Path,
  val npmBinary: Path,
  val npxBinary: Path,
  val projectRoot: Path = Files.createTempDirectory("codegen-node-sandbox-"),
  val projectVersion: String = "0.0.1",

  //TODO: add things from package json
) {

  init {
    if (Files.exists(projectRoot)) {
      require(Files.isDirectory(projectRoot))
    }

    require(Files.exists(nodeBinary)) { "Cannot find node binary at $nodeBinary" }
    require(Files.exists(npmBinary)) { "Cannot find npm binary at $npmBinary" }
    require(Files.exists(npxBinary)) { "Cannot find npx binary at $npxBinary" }

    require(projectVersion.isNotBlank()) { "projectVersion is required" }
  }


  val cleanProjectRoot = projectRoot.normalize().absolute()
}
