@file:JvmName("SQLDelightUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.LogicalFieldType


fun getSQLDelightTypeLiteral(type: LogicalFieldType): String = when (type.base) {

  //    BOOLEAN -> "INTEGER AS Boolean"
//    BYTE -> "INTEGER"
//    BYTE_ARRAY -> "BLOB"
//    CHAR -> "TEXT"
//    DOUBLE -> "REAL"
//    DURATION -> "TEXT"  //TODO: or long for millis?
//    FILE -> "TEXT"
//    FLOAT -> "REAL AS Float"
//    INSTANT -> "INTEGER AS Int"
//    INT -> "INTEGER AS Int"
//    LOCAL_DATE -> "TEXT"
//    LOCAL_DATE_TIME -> "TEXT"
//    LONG -> "INTEGER"
//    OFFSET_DATE_TIME -> "TEXT"
//    PATH -> "TEXT"
//    PERIOD -> "TEXT"
//    SHORT -> "INTEGER AS Short"
//    STRING -> "TEXT"
//    URI -> "TEXT"
//    URL -> "TEXT"
//    UUID -> "TEXT"

  else -> TODO()
}
