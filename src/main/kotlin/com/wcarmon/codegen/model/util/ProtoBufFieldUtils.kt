@file:JvmName("ProtoBufFieldUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.ast.Expression
import com.wcarmon.codegen.model.ast.RawStringExpression


fun buildProtoBufMessageFields(
  pkFields: Collection<Field>,
  nonPKFields: Collection<Field>,
): List<Expression> {

  var counter = 0
  val output = mutableListOf<Expression>()

  output += RawStringExpression("// -- PK field(s)")
  output += pkFields.map {
    counter++
    "${protobufTypeLiteral(it.type)} ${it.name.lowerSnake} = $counter;"
  }.map {
    RawStringExpression(it)
  }

  output += RawStringExpression("")
  output += RawStringExpression("// -- Other fields")

  output += nonPKFields.map {
    counter++
    "${protobufTypeLiteral(it.type)} ${it.name.lowerSnake} = $counter;"
  }.map {
    RawStringExpression(it)
  }

  return output
}


//TODO: get the effective protobuf type (allows user override in json config)
/**
 * See https://developers.google.com/protocol-buffers/docs/proto3#scalar
 */
private fun protobufTypeLiteral(
  type: LogicalFieldType,
): String = when (type.base) {

  BOOLEAN -> "bool"
//  CHAR -> TODO()

  DURATION,
  PATH,
  MONTH_DAY,
  PERIOD,
  STRING,
  URI,
  URL,
  UUID,
  YEAR_MONTH,
  ZONE_AGNOSTIC_DATE,
  ZONE_AGNOSTIC_TIME,
  -> "string"

  FLOAT_32 -> "float"
  FLOAT_64 -> "double"
  INT_16 -> "int32"
  INT_32 -> "int32"
  INT_64, UTC_INSTANT -> "int64"
//  ARRAY -> TODO()   //TODO: repeated
//  FLOAT_BIG -> TODO()
//  INT_128 -> TODO()
//  INT_8 -> TODO()
//  INT_BIG -> TODO()
//  LIST -> TODO()    //TODO: repeated
//  MAP -> TODO()
//  SET -> TODO()     //TODO: repeated
//  USER_DEFINED -> TODO()
//  UTC_TIME -> TODO()
//  YEAR -> TODO()
//  ZONE_OFFSET -> TODO()
//  ZONED_DATE_TIME -> TODO()

  else -> "FIX_${type.base}"
}
