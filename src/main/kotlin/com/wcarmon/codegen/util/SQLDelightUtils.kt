@file:JvmName("SQLDelightUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.QuoteType
import com.wcarmon.codegen.model.QuoteType.DOUBLE
import com.wcarmon.codegen.model.QuoteType.NONE


fun effectiveSQLDelightTypeLiteral(field: Field): String {

  val base =
    if (field.rdbmsConfig.overrideEffectiveType.isNotBlank()) {
      BaseFieldType.parse(field.rdbmsConfig.overrideEffectiveType)

    } else {
      field.type.base
    }

  return sqlDelightTypeLiteral(base)
}


fun quoteTypeForSQLDelightLiteral(base: BaseFieldType): QuoteType = when (base) {

  CHAR -> DOUBLE

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
