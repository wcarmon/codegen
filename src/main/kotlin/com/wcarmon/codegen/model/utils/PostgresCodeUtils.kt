@file:JvmName("PostgresCodeUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType

// For aligning columns
private val CHARS_FOR_COLUMN_NAME = 19
private val CHARS_FOR_COLUMN_TYPE = 9
private val CHARS_FOR_DEFAULT_CLAUSE = 13
private val CHARS_FOR_NULLABLE_CLAUSE = 9


//TODO: handle enums
fun asPostgreSQL(type: LogicalFieldType, varcharLength: Int = 0): String {
  require(varcharLength >= 0) { "varcharLength too low: $varcharLength" }

  return when (type.base) {
    ARRAY -> TODO()
    BOOLEAN -> "BOOLEAN"
    CHAR -> TODO()
    DURATION -> "INTERVAL"
    FLOAT_32 -> "FLOAT4"
    FLOAT_64 -> "FLOAT8"
    FLOAT_BIG -> TODO()
    INT_128 -> TODO()
    INT_16 -> "INT2"
    INT_32 -> "INT4"
    INT_64 -> "INT8"
    INT_8 -> "SMALLINT"
    INT_BIG -> TODO()
    LIST -> TODO()  // comma separated?
    MAP -> TODO()
    MONTH_DAY -> "VARCHAR(16)"
    PERIOD -> "INTERVAL"
    SET -> TODO() // comma separated?
    UTC_INSTANT -> "TIMESTAMP WITHOUT TIME ZONE"
    UTC_TIME -> TODO()
    UUID -> "UUID"
    YEAR -> "INT4"
    YEAR_MONTH -> "VARCHAR(32)"
    ZONE_AGNOSTIC_DATE -> "DATE"
    ZONE_AGNOSTIC_TIME -> TODO()
    ZONE_OFFSET -> "INT4"
    ZONED_DATE_TIME -> TODO()

    //TODO: allow param to override
    PATH -> "VARCHAR(256)"
    URL -> "VARCHAR(2048)"
    URI,
    STRING,
    -> "VARCHAR($varcharLength)"

    USER_DEFINED -> TODO("convert $type")
  }
}


fun postgresColumnDefinition(field: Field): String {
  val parts = mutableListOf<String>()

  parts += "\"${field.name.lowerSnake}\"".padEnd(CHARS_FOR_COLUMN_NAME, ' ')

  //TODO: fix this
//  parts += asSQLite(field.type).padEnd(CHARS_FOR_COLUMN_TYPE, ' ')

  // -- nullable clause
  val nullableClause =
    if (!field.type.nullable) "NOT NULL"
    else ""

  parts += nullableClause.padEnd(CHARS_FOR_NULLABLE_CLAUSE, ' ')

  //TODO: fix this
//  // -- default clause
//  val defaultClause =
//    if (field.hasDefault) "DEFAULT ${sqliteDefaultValueLiteral(field)}"
//    else ""
//
//  parts += defaultClause.padEnd(CHARS_FOR_DEFAULT_CLAUSE, ' ')

  return parts.joinToString(" ")
}
