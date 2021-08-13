@file:JvmName("KotlinCodeUtils")

/** Utilities only useful for generating Kotlin */
package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType

//TODO: handle unsigned types
fun asKotlin(type: LogicalFieldType) = when (type.base) {
  BaseFieldType.ARRAY -> getKotlinArrayType(type.base, type.typeParameters)
  BaseFieldType.BOOLEAN -> "Boolean"
  BaseFieldType.CHAR -> "Char"
  BaseFieldType.DURATION -> "kotlin.time.Duration"
  BaseFieldType.FLOAT_32 -> "Float"
  BaseFieldType.FLOAT_64 -> "Double"
  BaseFieldType.FLOAT_BIG -> "java.math.BigDecimal"
  BaseFieldType.INT_128 -> "java.math.BigInteger"
  BaseFieldType.INT_16 -> "Short"
  BaseFieldType.INT_32 -> "Int"
  BaseFieldType.INT_64 -> "Long"
  BaseFieldType.INT_8 -> "Byte"
  BaseFieldType.INT_BIG -> "java.math.BigInteger"
  BaseFieldType.LIST -> "List<${type.typeParameters[0]}>"
  BaseFieldType.MAP -> "Map<${type.typeParameters[0]}, ${type.typeParameters[1]}>"
  BaseFieldType.MONTH_DAY -> "java.time.MonthDay"
  BaseFieldType.PATH -> "java.nio.file.Path"
  BaseFieldType.PERIOD -> "java.time.Period"
  BaseFieldType.SET -> "Set<${type.typeParameters[0]}>"
  BaseFieldType.STRING -> "String"
  BaseFieldType.URI -> "java.net.URI"
  BaseFieldType.URL -> "java.net.URL"
  BaseFieldType.UTC_INSTANT -> "java.time.Instant"
  BaseFieldType.UTC_TIME -> "java.time.OffsetTime"
  BaseFieldType.UUID -> "java.util.UUID"
  BaseFieldType.YEAR -> "java.time.Year"
  BaseFieldType.YEAR_MONTH -> "java.time.YearMonth"
  BaseFieldType.ZONE_AGNOSTIC_DATE -> "java.time.LocalDate"
  BaseFieldType.ZONE_AGNOSTIC_TIME -> "java.time.LocalTime"
  BaseFieldType.ZONE_OFFSET -> "java.time.ZoneOffset"
  BaseFieldType.ZONED_DATE_TIME -> "java.time.ZonedDateTime"
  BaseFieldType.USER_DEFINED -> type.rawTypeLiteral //TODO: might need to convert if specified in non-jvm

}.let {
  if (type.nullable) "$it?" else it
}


/**
 * See https://kotlinlang.org/docs/basic-types.html
 *
 * @return kotlin array type literal
 */
private fun getKotlinArrayType(
  base: BaseFieldType,
  typeParameters: List<String>,
): String {
  check(base == BaseFieldType.ARRAY) { "Only invoke for arrays" }
  check(typeParameters.size == 1) { "exactly 1 type param required" }

  if (
    setOf(
      "byte",
      "int8",
      "java.lang.byte",
      "kotlin.byte",
    ).contains(typeParameters[0].lowercase())
  ) {
    return "ByteArray"
  }

  if (
    setOf(
      "int",
      "int32",
      "integer",
      "java.lang.int",
      "java.lang.integer",
      "kotlin.int",
    ).contains(typeParameters[0].lowercase())
  ) {
    return "IntArray"
  }

  if (
    setOf(
      "double",
      "float64",
      "java.lang.double",
      "kotlin.double",
    ).contains(typeParameters[0].lowercase())
  ) {
    return "DoubleArray"
  }

  TODO("determine kotlin array type: base=$base, typeParameters=$typeParameters")
}


/**
 * @return true when JVM compiler cannot automatically resolve the type
 */
fun kotlinTypeRequiresImport(fullyQualifiedJavaType: String): Boolean {
  if (fullyQualifiedJavaType.startsWith("java.lang.")) {
    return false
  }

  if (fullyQualifiedJavaType.startsWith("kotlin.")) {
    return false
  }

  // primitives
  if (!fullyQualifiedJavaType.contains(".")) {
    return false
  }

  return true
}

/**
 * @return comma separated method args clause
 */
fun kotlinMethodArgsForFields(fields: Collection<Field>) =
  fields
    .map { "${it.name.lowerCamel}: ${asKotlin(it.type)}" }
    .joinToString(", ")
