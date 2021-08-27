package com.wcarmon.codegen.model

/**
 * See https://swagger.io/specification/
 */
data class SwaggerType(
  val type: String,
  val format: String = "",
)
