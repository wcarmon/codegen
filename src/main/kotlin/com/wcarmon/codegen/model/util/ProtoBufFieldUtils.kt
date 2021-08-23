@file:JvmName("ProtoBufFieldUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.ast.Expression
import com.wcarmon.codegen.model.ast.FieldReadExpression
import com.wcarmon.codegen.model.ast.RawStringExpression
import com.wcarmon.codegen.model.ast.SerdeReadExpression


fun buildProtoBufMessageFieldDeclarations(
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

fun buildSerdeReadExpression(
  field: Field,
  fieldReadPrefix: String = "",
  fieldReadStyle: FieldReadStyle,
  serdeMode: SerdeMode,
) =
  SerdeReadExpression.forSerde(
    fieldReadExpression = FieldReadExpression(
      fieldName = field.name,
      fieldReadPrefix = fieldReadPrefix,
      overrideFieldReadStyle = fieldReadStyle,
    ),
    mode = serdeMode,
    serde = effectiveProtoSerde(field),
  )

//TODO: make an expression type, and return that
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


private fun effectiveProtoSerde(field: Field): Serde =
  if (field.protobuf.serde != null) {
    // -- User override is highest priority
    field.protobuf.serde

  } else if (requiresProtoSerde(field)) {
    // -- Fallback to jvm serializer
    defaultJVMSerde(field)

  } else {
    Serde.INLINE
  }


/**
 * @return true when Type is not trivially mapped to Proto field type
 */
private fun requiresProtoSerde(field: Field): Boolean =
  field.effectiveBaseType in setOf(PATH, URI, URL)
      || field.effectiveBaseType.isTemporal
      || field.isCollection
      || field.type.enumType


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
  MONTH_DAY,
  PATH,
  PERIOD,
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
  -> "string"

  FLOAT_32 -> "float"
  FLOAT_64 -> "double"

  INT_16,
  INT_32,
  -> "int32"

  INT_64,
  YEAR,
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
//  ZONE_OFFSET -> TODO()


  else -> "//TODO: fix TYPE=${type.base}"
}
