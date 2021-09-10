package com.wcarmon.codegen.sandbox

import java.util.*

/**
 * See sandbox.properties
 */
data class SandboxConfig(
  val gradleConfig: GradleConfig,
  val nodeConfig: NodeConfig,
) {

  companion object {

    @JvmStatic
    fun fromProperties(
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

          return SandboxConfig(
            gradleConfig = GradleConfig.parseProperties(props),
            nodeConfig = NodeConfig.parseProperties(props),
          )
        }
    }
  }
}
