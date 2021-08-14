@file:JvmName("JVMCodeUtils")

/** Utilities common to all JVM languages */
package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.QuoteType.*

/**
 * Output only applicable to JVM languages (eg. Java, Kotlin, groovy...)
 *
 * @return Quote type for the logical base type
 */
fun quoteTypeForJVMLiterals(base: BaseFieldType) = when (base) {

  CHAR -> SINGLE

  BOOLEAN,
  FLOAT_32,
  FLOAT_64,
  INT_128,
  INT_16,
  INT_32,
  INT_64,
  INT_8,
  YEAR,
  ZONE_OFFSET,
  -> NONE

  FLOAT_BIG,
  INT_BIG,
  -> TODO("Determine quote type for JVM literal: $base")

  else -> DOUBLE
}

/**
 * Output only applicable to JVM languages (eg. Java, Kotlin, groovy...)
 *
 * @return the default value literal
 */
fun defaultValueLiteralForJVM(field: Field): String? {
  if (field.defaultValue == null) {
    return null
  }

  if (field.shouldDefaultToNull) {
    return "null"
  }

  return quoteTypeForJVMLiterals(field.type.base)
    .wrap(field.defaultValue)
}


/**
 * Expands the template
 * Useful for Jackson, and json stores (eg. ElasticSearch, MongoDB, ...)
 *
 * See [LogicalFieldType.jvmDeserializeTemplate]
 *
 * @return expanded template (with %s replaced with [fieldValueExpression])
 */
fun expandJVMDeserializeTemplate(
  field: Field,
  fieldValueExpression: String,
): String {
  val type = field.type
  check(shouldUseJVMSerde(field)) {
    "only invoke when we should use jvm deserializer"
  }

  if (type.jvmDeserializeTemplate.isNotBlank()) {
    return String.format(
      type.jvmDeserializeTemplate,
      fieldValueExpression)
  }

  if (type.base.isTemporal
    || type.base in setOf(PATH, URI, URL)
    || type.enumType
  ) {
    return String.format(
      defaultJavaDeserializeTemplate(type),
      fieldValueExpression)
  }

  TODO("decide how to deserialize on jvm: $type")
}

//TODO: document me
private fun shouldUseJVMSerde(field: Field): Boolean =
  field.hasCustomJVMSerde
      || field.effectiveBaseType == PATH
      || field.effectiveBaseType == URI
      || field.effectiveBaseType == URL
      || field.effectiveBaseType.isTemporal
      || field.type.enumType
