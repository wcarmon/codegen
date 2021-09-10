package com.wcarmon.codegen.sandbox

import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.io.path.absolute


fun Properties.parseOptionalString(
  propertyName: String,
  defaultIfBlank: String,
): String {
  val value = getProperty(propertyName, "").trim()

  if (value.isNotBlank()) {
    return defaultIfBlank
  }

  return value
}

fun Properties.parseRequiredString(
  propertyName: String,
): String {
  val value = getProperty(propertyName, "").trim()
  require(value.isNotBlank()) { "missing required property: $propertyName" }

  return value
}

fun Properties.parseRequiredPath(
  propertyName: String,
): Path {
  val value = getProperty(propertyName, "").trim()
  require(value.isNotBlank()) { "missing required Path property: $propertyName" }

  return Paths.get(value).normalize().absolute()
}

fun Properties.parseRequiredBoolean(
  propertyName: String,
): Boolean {

  val value = getProperty(propertyName, "").trim().lowercase()

  check(value == "true" || value == "false") {
    "invalid boolean property: $propertyName"
  }

  return value == "true"
}

fun Properties.parseOptionalBoolean(
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
