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

  val output = mutableListOf<Expression>()

  output += RawStringExpression("// -- PK field(s)")

  output += buildFieldDeclarationExpressions(pkFields, 1)

  output += RawStringExpression("")
  output += RawStringExpression("// -- Other fields")

  output += buildFieldDeclarationExpressions(
    nonPKFields, 1 + pkFields.size)

  return output
}

private fun buildFieldDeclarationExpressions(
  fields: Collection<Field>,
  firstFieldNumber: Int = 1,
): List<Expression> =
  fields.mapIndexed { index, field ->

    val repeatedPrefix =
      if (field.protobuf.repeated) "repeated "
      else ""

    "${repeatedPrefix}${effectiveType(field)} ${field.name.lowerSnake} = ${index + firstFieldNumber};"

  }.map {
    RawStringExpression(it)
  }


private fun effectiveType(field: Field): String {

  if (field.protobuf.overrideTypeLiteral.isNotBlank()) {
    return field.protobuf.overrideTypeLiteral
  }

  return protobufTypeLiteral(field.type)
}

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
  ZONED_DATE_TIME,
  -> "string"

  FLOAT_32 -> "float"
  FLOAT_64 -> "double"

  INT_16,
  INT_32,
  -> "int32"

  INT_64,
  YEAR,
  UTC_INSTANT,
  -> "int64"

  USER_DEFINED -> TODO("Add a type override in field.protobuf.overrideTypeLiteral: $type")

//  ARRAY -> TODO()   //TODO: repeated
//  FLOAT_BIG -> TODO()
//  INT_128 -> TODO()
//  INT_8 -> TODO()
//  INT_BIG -> TODO()
//  LIST -> TODO()    //TODO: repeated
//  MAP -> TODO()
//  SET -> TODO()     //TODO: repeated
//  UTC_TIME -> TODO()
//  ZONE_OFFSET -> TODO()


  else -> "//TODO: fix TYPE=${type.base}"
}
