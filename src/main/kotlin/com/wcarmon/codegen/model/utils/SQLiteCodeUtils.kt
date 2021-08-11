@file:JvmName("SQLiteCodeUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.QuoteType
import com.wcarmon.codegen.model.QuoteType.NONE
import com.wcarmon.codegen.model.QuoteType.SINGLE

// Quoting info
// https://www.sqlite.org/lang_keywords.html

// For aligning columns
private val CHARS_FOR_COLUMN_NAME = 19
private val CHARS_FOR_COLUMN_TYPE = 7
private val CHARS_FOR_DEFAULT_CLAUSE = 13
private val CHARS_FOR_NULLABLE_CLAUSE = 9

/**
 * See https://www.sqlite.org/datatype3.html
 *
 * SQLite adjust storage based on the values you pass it
 */
fun asSQLite(type: LogicalFieldType) =
  when (type.base) {

    BOOLEAN,
    INT_16,
    INT_32,
    INT_64,
    INT_8,
    YEAR,
    ZONE_OFFSET,
    -> "INTEGER"

    FLOAT_32,
    FLOAT_64,
    FLOAT_BIG,
    -> "REAL"

    INT_BIG,
    INT_128,
    -> TODO("decide how to handle large ints: $type")

    //byte[] -> BLOB
    //other arrays -> ?
    ARRAY -> TODO("decide how to handle array: $type")

    else -> "TEXT"
  }

fun quoteTypeForSQLiteLiterals(base: BaseFieldType): QuoteType = when (base) {

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

  else -> SINGLE
}


/**
 * See https://www.sqlite.org/lang_createtable.html
 * See https://www.sqlite.org/lang_createtable.html#tablecoldef
 */
fun sqliteColumnDefinition(field: Field): String {
  val parts = mutableListOf<String>()

  parts += "\"${field.name.lowerSnake}\"".padEnd(CHARS_FOR_COLUMN_NAME, ' ')

  parts += asSQLite(field.type).padEnd(CHARS_FOR_COLUMN_TYPE, ' ')

  // -- nullable clause
  val nullableClause =
    if (!field.type.nullable) "NOT NULL"
    else ""

  parts += nullableClause.padEnd(CHARS_FOR_NULLABLE_CLAUSE, ' ')

  // -- default clause
  val defaultClause =
    if (field.hasDefault) "DEFAULT ${sqliteDefaultValueLiteral(field)}"
    else ""

  parts += defaultClause.padEnd(CHARS_FOR_DEFAULT_CLAUSE, ' ')

  return parts.joinToString(" ")
}

//TODO: more tests here
fun sqliteDefaultValueLiteral(field: Field): String {
  if (field.defaultValue == null) {
    return ""
  }

  if (field.shouldDefaultToNull) {
    return "NULL"
  }

  return quoteTypeForSQLiteLiterals(field.type.base)
    .wrap(field.defaultValue)
}
