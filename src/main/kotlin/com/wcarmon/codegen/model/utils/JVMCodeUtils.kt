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
 * See [LogicalFieldType.jvmDeserializerTemplate]
 *
 * @return expanded template (with %s replaced with [fieldValueExpression])
 */
fun expandJVMDeserializeTemplate(
  field: Field,
  fieldValueExpression: String,
): String {
  val type = field.type
  check(shouldUseJVMDeserializer(field)) {
    "only invoke when we should use jvm deserializer"
  }

  if (type.jvmDeserializerTemplate.isNotBlank()) {
    return String.format(
      type.jvmDeserializerTemplate,
      fieldValueExpression)
  }

  if (type.base.isTemporal
    || type.base in setOf(PATH, URI, URL)
    || type.enumType
  ) {
    return String.format(
      defaultJavaDeserializerTemplate(type),
      fieldValueExpression)
  }

  TODO("decide how to deserialize on jvm: $type")
}

//TODO: document me
fun shouldUseJVMDeserializer(field: Field): Boolean =
  field.hasCustomJVMSerde
      || field.type.base == PATH
      || field.type.base == URI
      || field.type.base == URL
      || field.type.base.isTemporal
      || field.type.enumType
