package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*

/*
 * Mappings from BaseType/Field to String (Type Literals)
 */


//TODO: handle unsigned types
fun fullyQualifiedKotlinTypeLiteral(
  field: Field,
): String {
  val baseType = field.effectiveBaseType(TargetLanguage.KOTLIN_JVM_1_4)
  val typeParameters = field.typeParameters(TargetLanguage.KOTLIN_JVM_1_4)

  return when (baseType) {
    ARRAY -> kotlinArrayType(baseType, typeParameters)
    BOOLEAN -> "Boolean"
    BYTE_ARRAY -> "ByteArray"
    CHAR -> "Char"
    COLOR -> "String"
//    DURATION -> "kotlin.time.Duration"
    DURATION -> "java.time.Duration"
    EMAIL -> "String"
    FLOAT_32 -> "Float"
    FLOAT_64 -> "Double"
    FLOAT_BIG -> "java.math.BigDecimal"
    INT_128 -> "java.math.BigInteger"
    INT_16 -> "Short"
    INT_32 -> "Int"
    INT_64 -> "Long"
    INT_8 -> "Byte"
    INT_BIG -> "java.math.BigInteger"
    LIST -> "List<${typeParameters[0]}>"
    MAP -> "Map<${typeParameters[0]}, ${typeParameters[1]}>"
    MONTH_DAY -> "java.time.MonthDay"
    PATH -> "java.nio.file.Path"
    PERIOD -> "java.time.Period"
    PHONE_NUMBER -> "String"
    SET -> "Set<${typeParameters[0]}>"
    STRING -> "String"
    URI -> "java.net.URI"
    URL -> "java.net.URL"
    UTC_INSTANT -> "java.time.Instant"
    UTC_TIME -> "java.time.OffsetTime"
    UUID -> "java.util.UUID"
    YEAR -> "java.time.Year"
    YEAR_MONTH -> "java.time.YearMonth"
    ZONE_AGNOSTIC_DATE -> "java.time.LocalDate"
    ZONE_AGNOSTIC_DATE_TIME -> "java.time.LocalDateTime"
    ZONE_AGNOSTIC_TIME -> "java.time.LocalTime"
    ZONE_OFFSET -> "java.time.ZoneOffset"
    ZONED_DATE_TIME -> "java.time.ZonedDateTime"

    //TODO: might need to convert if specified in non-jvm
    USER_DEFINED -> field.type.rawTypeLiteral
    WEEK_OF_YEAR -> TODO()
  }.let {
    if (field.type.nullable) "$it?" else it
  }
}


@Suppress("ComplexMethod")
fun fullyQualifiedJavaTypeLiteral(
  field: Field,
): String {

  val baseType = field.effectiveBaseType(TargetLanguage.JAVA_08)
  val typeParameters = field.typeParameters(TargetLanguage.JAVA_08)

  return when (baseType) {

    BYTE_ARRAY -> "byte[]"
    ARRAY -> typeParameters.first() + "[]"
    BOOLEAN -> if (field.type.nullable) "Boolean" else "boolean"
    CHAR -> if (field.type.nullable) "Character" else "char"
    COLOR -> "String"
    DURATION -> "java.time.Duration"
    EMAIL -> "String"
    FLOAT_32 -> if (field.type.nullable) "Float" else "float"
    FLOAT_64 -> if (field.type.nullable) "Double" else "double"
    FLOAT_BIG -> "java.math.BigDecimal"
    INT_128 -> "java.math.BigInteger"
    INT_16 -> if (field.type.nullable) "Short" else "short"
    INT_32 -> if (field.type.nullable) "Integer" else "int"
    INT_64 -> if (field.type.nullable) "Long" else "long"
    INT_8 -> if (field.type.nullable) "Byte" else "byte"
    INT_BIG -> "java.math.BigInteger"
    LIST -> "java.util.List<${typeParameters[0]}>"
    MAP -> "java.util.Map<${typeParameters[0]}, ${typeParameters[1]}>"
    MONTH_DAY -> "java.time.MonthDay"
    PATH -> "java.nio.file.Path"
    PERIOD -> "java.time.Period"
    PHONE_NUMBER -> "String"
    SET -> "java.util.Set<${typeParameters[0]}>"
    STRING -> "String"
    URI -> "java.net.URI"
    URL -> "java.net.URL"
    UTC_INSTANT -> "java.time.Instant"
    UTC_TIME -> "java.time.OffsetTime"
    UUID -> "java.util.UUID"
    YEAR -> "java.time.Year"
    YEAR_MONTH -> "java.time.YearMonth"
    ZONE_AGNOSTIC_DATE -> "java.time.LocalDate"
    ZONE_AGNOSTIC_DATE_TIME -> "java.time.LocalDateTime"
    ZONE_AGNOSTIC_TIME -> "java.time.LocalTime"
    ZONE_OFFSET -> "java.time.ZoneOffset"
    ZONED_DATE_TIME -> "java.time.ZonedDateTime"

    WEEK_OF_YEAR -> TODO()

    //TODO: need to convert when raw is specified in json as non-jvm
    USER_DEFINED -> field.type.rawTypeLiteral
  }
}


/**
 * See https://www.postgresql.org/docs/current/datatype.html
 *
 * Derive the correct type
 *
 * @return Literal for PostgreSQL type
 */
fun postgresTypeLiteral(
  logicalFieldType: LogicalFieldType,
  rdbmsConfig: RDBMSColumnConfig,
  effectiveBaseType: BaseFieldType = logicalFieldType.base,
  errorLoggingInfo: String,
): String = when (effectiveBaseType) {
  BOOLEAN -> "BOOLEAN"
  BYTE_ARRAY -> "BYTEA"
  CHAR -> "VARCHAR(4)"
  DURATION -> "VARCHAR(40)"         // only need 37
  FLOAT_32 -> "REAL"                // FLOAT4
  FLOAT_64 -> "DOUBLE PRECISION"    // FLOAT8
  INT_128 -> "NUMERIC(20,0)"
  INT_16 -> "SMALLINT"              // INT2 == NUMERIC(3,0)
  INT_64 -> "BIGINT"                // INT8 == NUMERIC(10,0)
  INT_8 -> "SMALLINT"               // INT2 is the smallest, NUMERIC(2,0)
  MONTH_DAY -> "VARCHAR(16)"
  PATH -> "VARCHAR(256)"
  PERIOD -> "VARCHAR(40)"           // only need 37
  URL -> "VARCHAR(2048)"
  UTC_INSTANT -> "VARCHAR(32)"      // only need 27
  UTC_TIME -> "VARCHAR(16)"         // only need 15
  UUID -> "VARCHAR(36)"             // PostgreSQL has a UUID type, but why bother :-)
  YEAR_MONTH -> "VARCHAR(32)"
  ZONE_AGNOSTIC_DATE -> "VARCHAR(16)"
  ZONE_AGNOSTIC_TIME -> "VARCHAR(16)" // only need 12
  ZONED_DATE_TIME -> "VARCHAR(68)"    // some timezone names are long
  INT_32,
  YEAR,
  ZONE_OFFSET,
  -> "INTEGER"                        // INT4 == NUMERIC(5,0)

  INT_BIG -> {
    requireNotNull(logicalFieldType.precision) {
      "Positive field.precision is required: $errorLoggingInfo"
    }

    "NUMERIC(${logicalFieldType.precision}, 0)"
  }

  FLOAT_BIG -> {
    require(logicalFieldType.scale > 0) {
      "Positive field.scale is required for float types: $errorLoggingInfo"
    }

    "NUMERIC(${logicalFieldType.precision}, ${logicalFieldType.scale})"
  }

  COLOR -> "VARCHAR(7)"

  ARRAY,
  EMAIL,
  LIST,
  MAP,
  PHONE_NUMBER,
  SET,
  STRING,
  URI,
  USER_DEFINED,
  -> {
    requireNotNull(rdbmsConfig.varcharLength) {
      "field.rdbms.varcharLength (or field.rdbms.overrideEffectiveType) is required: $errorLoggingInfo"
    }

    "VARCHAR(${rdbmsConfig.varcharLength})"
  }
  WEEK_OF_YEAR -> TODO()
  ZONE_AGNOSTIC_DATE_TIME -> TODO()
}


/**
 * See https://cashapp.github.io/sqldelight/jvm_sqlite/types/
 */
fun sqlDelightTypeLiteral(baseType: BaseFieldType): String = when (baseType) {

  BOOLEAN -> "INTEGER AS Boolean"
  CHAR -> TODO()
  FLOAT_32 -> "REAL AS Float"
  FLOAT_64 -> "REAL"
  FLOAT_BIG -> TODO()
  INT_128 -> TODO()
  INT_16 -> "INTEGER AS Short"
  INT_32 -> "INTEGER AS Int"
  INT_64 -> "INTEGER"
  INT_8 -> TODO()
  INT_BIG -> TODO()
  YEAR -> "INTEGER"
  ZONE_OFFSET -> "INTEGER"

  ARRAY,
  COLOR,
  DURATION,
  EMAIL,
  LIST,
  MAP,
  MONTH_DAY,
  PATH,
  PERIOD,
  PHONE_NUMBER,
  SET,
  STRING,
  URI,
  URL,
  UTC_INSTANT,
  UTC_TIME,
  UUID,
  YEAR_MONTH,
  ZONE_AGNOSTIC_DATE,
  ZONE_AGNOSTIC_TIME,
  ZONED_DATE_TIME,
  -> "TEXT"

  //TODO: allow override in json config
  USER_DEFINED -> "TEXT"

  else -> TODO("get sqldelight type for: $baseType")
}


//TODO: handle enums
fun typescriptTypeLiteral(base: BaseFieldType): String = when (base) {

  BYTE_ARRAY -> TODO()

  BOOLEAN -> "boolean"
  UTC_INSTANT -> "Date"

  CHAR,
  COLOR,
  DURATION,
  EMAIL,
  MONTH_DAY,
  PATH,
  PERIOD,
  PHONE_NUMBER,
  STRING,
  URI,
  URL,
  UTC_TIME,
  UUID,
  YEAR_MONTH,
  ZONE_AGNOSTIC_DATE,
  ZONE_AGNOSTIC_DATE_TIME,
  ZONE_AGNOSTIC_TIME,
  ZONED_DATE_TIME,
  -> "string"

  FLOAT_32,
  FLOAT_64,
  FLOAT_BIG,
  INT_128,
  INT_16,
  INT_32,
  INT_64,
  INT_8,
  INT_BIG,
  WEEK_OF_YEAR,
  ZONE_OFFSET,
  -> "number"

  ARRAY,
  LIST,
  -> TODO("handle ts arrays")

  SET -> TODO("Set or array?")
  MAP -> TODO("object or Map?")
  YEAR -> TODO("handle year")

  USER_DEFINED -> TODO("convert $base")
}


/**
 * See https://kotlinlang.org/docs/basic-types.html
 *
 * @return kotlin array type literal
 */
@Suppress("ReturnCount")
private fun kotlinArrayType(
  base: BaseFieldType,
  typeParameters: List<String>,
): String {
  check(base == ARRAY) { "Only invoke for arrays" }
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
