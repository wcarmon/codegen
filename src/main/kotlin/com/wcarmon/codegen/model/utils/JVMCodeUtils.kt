@file:JvmName("JVMCodeUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.QuoteType
import com.wcarmon.codegen.model.QuoteType.*

fun quoteTypeForJVMLiterals(base: BaseFieldType): QuoteType = when (base) {

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

fun jdbcGetter(type: LogicalFieldType): String = when (type.base) {
  BOOLEAN -> "getBoolean"
  FLOAT_32 -> "getFloat"
  FLOAT_64 -> "getDouble"
  FLOAT_BIG -> "getBigDecimal"
  INT_16 -> "getShort"
  INT_32 -> "getInt"
  INT_64 -> "getLong"
  URL -> "getURL"
  UTC_INSTANT -> "getString"
  YEAR -> "getInt"

//    CHAR, // 16-bit Unicode character
//    INT_128
//    INT_8
//    INT_BIG

//    DURATION,           // measured in seconds & nanos
//    MONTH_DAY,          // eg. birthdays
//    PERIOD,             // measured in years, months (day agnostic) or days (time agnostic)
//    UTC_TIME,           // eg. daily meeting time, market opening time (y/m/d agnostic)
//    YEAR_MONTH,         // eg. credit card expiration
//    ZONE_AGNOSTIC_DATE, // eg. birthdate (tz agnostic)
//    ZONE_AGNOSTIC_TIME, // eg. store closing time (tz agnostic)
//    ZONE_OFFSET,        // seconds (with upper bound)
//    ZONED_DATE_TIME,    // Instant + offset + tz rules

  ARRAY,
  LIST,
  MAP,
  PATH,
  SET,
  STRING,
  URI,
  UUID,
  -> "getString"


  //TODO: handle when enum puts number in database
  USER_DEFINED ->
    if (type.enumType) "getString"
    else "getString"  // User has to serialize somehow

  else -> TODO("add jdbc getter for $type")
}


/**
 * See [LogicalFieldType.jvmDeserializerTemplate]
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

  TODO("decide how to deserialise on jvm: $type")
}

fun shouldUseJVMDeserializer(type: LogicalFieldType): Boolean =
  type.jvmSerializerTemplate.isNotBlank()
      || type.base == PATH
      || type.base == URI
      || type.base == URL
      || type.base.isTemporal
      || type.enumType
