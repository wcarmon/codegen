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
 * See https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/ResultSet.html
 *
 * @return getter method literal declared on [java.sql.ResultSet]
 */
fun jdbcGetter(type: LogicalFieldType): String = when (type.base) {
  BOOLEAN -> "getBoolean"
  FLOAT_32 -> "getFloat"
  FLOAT_64 -> "getDouble"
  FLOAT_BIG -> "getBigDecimal"
  INT_16 -> "getShort"
  INT_32 -> "getInt"
  INT_64 -> "getLong"
  INT_8 -> "getByte"
  URL -> "getURL"
  YEAR -> "getInt"
  ZONE_OFFSET -> "getInt"

  ARRAY,
  DURATION,
  LIST,
  MAP,
  MONTH_DAY,
  PATH,
  PERIOD,
  SET,
  STRING,
  URI,
  USER_DEFINED,
  UTC_INSTANT,
  UTC_TIME,
  UUID,
  YEAR_MONTH,
  ZONE_AGNOSTIC_DATE,
  ZONE_AGNOSTIC_TIME,
  ZONED_DATE_TIME,
  -> "getString"

  // TODO: CHAR, // 16-bit Unicode character
  // TODO: INT_128
  // TODO: INT_BIG

  else -> TODO("Add jdbc getter for $type")
}


/**
 * Uses expands the template
 *
 * See [LogicalFieldType.jvmDeserializerTemplate]
 *
 * @return expanded template (with %s replaced with [fieldValueExpression])
 */
fun jvmDeserializeTemplate(
  type: LogicalFieldType,
  fieldValueExpression: String,
): String {
  require(shouldUseJVMDeserializer(type)) {
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

//TODO: use JVM Deserializer unless user overrides using [Field::jvmDeserializer]
fun shouldUseJVMDeserializer(type: LogicalFieldType): Boolean =
  type.jvmSerializerTemplate.isNotBlank()
      || type.base == PATH
      || type.base == URI
      || type.base == URL
      || type.base.isTemporal
      || type.enumType
