package com.wcarmon.codegen.model

/**
 * Determines whether to output 1 or multiple files
 */
enum class OutputMode {

  /** Generate multiple files (in a directory) */
  MULTIPLE,

  /** Generate one file (possibly containing multiple entities) */
  SINGLE;

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
