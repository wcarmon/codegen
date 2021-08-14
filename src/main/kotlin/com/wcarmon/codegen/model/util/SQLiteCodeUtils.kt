@file:JvmName("SQLiteCodeUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType


// For aligning columns
private const val CHARS_FOR_COLUMN_NAME = 20
private const val CHARS_FOR_COLUMN_TYPE = 7
private const val CHARS_FOR_DEFAULT_CLAUSE = 13
private const val CHARS_FOR_NULLABLE_CLAUSE = 9

/**
 * See https://www.sqlite.org/datatype3.html
 *
 * SQLite adjust storage based on the values you pass it
 */
fun getSQLiteTypeLiteral(type: LogicalFieldType) =
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


/**
 * See https://www.sqlite.org/lang_createtable.html
 * See https://www.sqlite.org/lang_createtable.html#tablecoldef
 */
fun sqliteColumnDefinition(field: Field): String {
  val parts = mutableListOf<String>()

  parts += "\"${field.name.lowerSnake}\""
    .padEnd(CHARS_FOR_COLUMN_NAME, ' ')

  parts += getSQLiteTypeLiteral(field.type)
    .padEnd(CHARS_FOR_COLUMN_TYPE, ' ')

  // -- nullable clause
  val nullableClause =
    if (!field.type.nullable) "NOT NULL"
    else ""

  parts += nullableClause.padEnd(CHARS_FOR_NULLABLE_CLAUSE, ' ')

  // -- default clause
  val defaultClause =
    if (field.hasDefault) "DEFAULT ${rdbmsDefaultValueLiteral(field)}"
    else ""

  parts += defaultClause.padEnd(CHARS_FOR_DEFAULT_CLAUSE, ' ')

  return parts.joinToString(" ")
}
