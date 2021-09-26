@file:JvmName("GolangCodeUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage.GOLANG_1_8


//TODO: Go: pointer mapping

@Suppress("ReturnCount")
fun golangTypeLiteral(
  field: Field,
  qualified: Boolean,
): String {

  val output = getFullyQualifiedGolangTypeLiteral(field)

  //TODO: handle generics here
  //TODO: use qualified here

  return output
}

//TODO: handle unsigned types
private fun getFullyQualifiedGolangTypeLiteral(
  field: Field,
): String {
  val typeParameters = field.typeParameters(GOLANG_1_8)

  return when (field.effectiveBaseType(GOLANG_1_8)) {
//  ARRAY -> getKotlinArrayType(type.base, type.typeParameters)
    BOOLEAN -> "bool"
    CHAR -> "rune"
    COLOR -> "string"
    DURATION -> "time.Duration"
    EMAIL -> "string"
    FLOAT_32 -> "float32"
    FLOAT_64 -> "float64"
    FLOAT_BIG -> "big.Float"
    INT_128 -> "big.Int"
    INT_16 -> "int16"
    INT_32 -> "int32"
    INT_64 -> "int64"
    INT_8 -> "int8"
    INT_BIG -> "big.Int"
//  LIST -> "List<${type.typeParameters[0]}>"
//  MAP -> "Map<${type.typeParameters[0]}, ${type.typeParameters[1]}>"
//  MONTH_DAY -> "java.time.MonthDay"
//  PATH -> "java.nio.file.Path"
//  PERIOD -> "java.time.Period"
    PHONE_NUMBER -> "string"
    SET -> "map[${typeParameters[0]}]bool"
    STRING -> "string"
    URI -> "url.URL"
    URL -> "url.URL"
    UTC_INSTANT -> "time.Time"
    UTC_TIME -> "time.Time"
//  UUID -> "java.util.UUID"
    YEAR -> "int64"
//  YEAR_MONTH -> "java.time.YearMonth"
//  ZONE_AGNOSTIC_DATE -> "java.time.LocalDate"
//  ZONE_AGNOSTIC_DATE_TIME -> "java.time.LocalDateTime"
//  ZONE_AGNOSTIC_TIME -> "java.time.LocalTime"
//  ZONE_OFFSET -> "java.time.ZoneOffset"
//  ZONED_DATE_TIME -> "java.time.ZonedDateTime"

    USER_DEFINED -> field.type.rawTypeLiteral //TODO: might need to convert if specified in non-jvm
//  WEEK_OF_YEAR -> TODO()

    else -> TODO("Add type mapping for golang: field=$field")

  }.let {
    if (field.type.nullable) "*${it}" else it
  }
}
