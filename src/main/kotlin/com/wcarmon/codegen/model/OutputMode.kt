package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Determines whether to output 1 or multiple files
 */
enum class OutputMode(
  @JsonValue
  val serialized: String,
) {

  /** Generate multiple files (in a directory) */
  MULTIPLE("multiple"),

  /** Generate one file (possibly containing multiple entities) */
  SINGLE("single");

  init {
    require(serialized.isNotBlank()) { "serialized value is required" }
  }

  companion object {

    @JvmStatic
    fun fromString(input: String): OutputMode {
      val normalized = input.trim()

      return values().firstOrNull {
        normalized.equals(it.name, ignoreCase = true) ||
            normalized.equals(it.toString(), ignoreCase = true)
      }
        ?: throw IllegalArgumentException("Failed to parse OutputMode: input=$input")
    }
  }
}
