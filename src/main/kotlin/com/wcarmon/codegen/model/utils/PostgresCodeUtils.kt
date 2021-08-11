@file:JvmName("PostgresCodeUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.utils.rdbmsDefaultValueLiteral

// For aligning columns
private val CHARS_FOR_COLUMN_NAME = 20
private val CHARS_FOR_COLUMN_TYPE = 12
private val CHARS_FOR_DEFAULT_CLAUSE = 13
private val CHARS_FOR_NULLABLE_CLAUSE = 9


//TODO: handle enums
fun getPostgresTypeLiteral(
  type: LogicalFieldType,
  varcharLength: Int = 256,
): String {
  require(varcharLength >= 0) { "varcharLength too low: $varcharLength" }

  return when (type.base) {
    BOOLEAN -> "BOOLEAN"
    CHAR -> "VARCHAR(4)"
    DURATION -> "VARCHAR(36)"
    FLOAT_32 -> "FLOAT4"
    FLOAT_64 -> "FLOAT8"
    INT_16 -> "INT2"
    INT_32 -> "INT4"
    INT_64 -> "INT8"
    INT_8 -> "SMALLINT"
    MONTH_DAY -> "VARCHAR(16)"
    PATH -> "VARCHAR(256)"
    PERIOD -> "INTERVAL"
    URL -> "VARCHAR(2048)"
    UTC_INSTANT -> "VARCHAR(27)"
    UTC_TIME -> "VARCHAR(15)"
    UUID -> "UUID"
    YEAR -> "INT4"
    YEAR_MONTH -> "VARCHAR(32)"
    ZONE_AGNOSTIC_TIME -> "VARCHAR(12)"
    ZONE_OFFSET -> "INT4"

    FLOAT_BIG,
    INT_128,
    INT_BIG,
    ZONE_AGNOSTIC_DATE, //TODO: varchar
    ZONED_DATE_TIME,
    MAP,
    -> TODO("handle getting pg type literal for $type")

    ARRAY,
    LIST,
    SET,
    -> "VARCHAR($varcharLength)"

    STRING,
    URI,
    USER_DEFINED,
    -> "VARCHAR($varcharLength)"
  }
}


fun postgresColumnDefinition(field: Field): String {
  val parts = mutableListOf<String>()

  parts += "\"${field.name.lowerSnake}\""
    .padEnd(CHARS_FOR_COLUMN_NAME, ' ')

  parts += getPostgresTypeLiteral(field.type)
    .padEnd(CHARS_FOR_COLUMN_TYPE, ' ')

  // -- nullable clause
  val nullableClause =
    if (!field.type.nullable) "NOT NULL"
    else ""

  parts += nullableClause.padEnd(CHARS_FOR_NULLABLE_CLAUSE, ' ')

  //TODO: fix this
  // -- default clause
  val defaultClause =
    if (field.hasDefault) "DEFAULT ${rdbmsDefaultValueLiteral(field)}"
    else ""

  parts += defaultClause.padEnd(CHARS_FOR_DEFAULT_CLAUSE, ' ')

  return parts.joinToString(" ")
}
