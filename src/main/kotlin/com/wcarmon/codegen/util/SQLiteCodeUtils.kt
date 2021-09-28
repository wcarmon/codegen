@file:JvmName("SQLiteCodeUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.LogicalFieldType


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
