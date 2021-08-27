@file:JvmName("ProtoBufFieldUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*


fun buildProtoBufMessageFieldDeclarations(
  pkFields: Collection<Field>,
  nonPKFields: Collection<Field>,
): List<Expression> {

  val output = mutableListOf<Expression>()

  output += RawExpression("// -- PK field(s)")

  output += buildFieldDeclarationExpressions(pkFields, 1)

  output += EmptyExpression
  output += RawExpression("// -- Other fields")

  output += buildFieldDeclarationExpressions(
    nonPKFields, 1 + pkFields.size)

  return output
}

fun buildSerdeReadExpression(
  field: Field,

  /** eg. "entity." or "proto." */
  fieldReadPrefix: String = "",
  fieldReadStyle: FieldReadMode,
  serdeMode: SerdeMode,
) =
  SerdeWrapExpression.forSerde(
    fieldReadExpression = FieldReadExpression(
      fieldName = field.name,
      fieldReadPrefix = fieldReadPrefix,
      overrideFieldReadStyle = fieldReadStyle,
    ),
    mode = serdeMode,
    serde = effectiveProtoSerde(field),
  )

//TODO: document me
fun protoBuilderSetter(field: Field): Name =
  if (field.isCollection) {
    "addAll"
  } else {
    "set"
  }
    .let { prefix ->
      Name("$prefix${field.name.upperCamel}")
    }


//TODO: document me
fun protoBuilderGetter(field: Field): Name =
  if (field.isCollection) {
    "getAll"
  } else {
    "get"
  }
    .let { prefix ->
      Name("$prefix${field.name.upperCamel}")
    }

/**
 * Useful for Collections & other Generic types
 *
 * @param field
 * @param fieldReadExpressions one identifier (or field reader) for each generic/type-param
 * @param serdeMode (serialize or deserialize)
 *
 * @return one SerdeReadExpression for each generic/type-param
 */
//TODO: make (or reuse) an expression type
fun protoReadExpressionForTypeParameters(
  field: Field,
  fieldReadExpressions: List<Expression>,
  serdeMode: SerdeMode,
): List<Expression> =
  effectiveProtoSerdeForTypeParameters(field)
    .mapIndexed { index, serdeForTypeParam ->
      SerdeWrapExpression(
        fieldReadExpression = fieldReadExpressions[index],
        serdeTemplate = serdeForTypeParam.forMode(serdeMode),
      )
    }

//TODO: improve documentation
// Get the collection fields, avoid duplicate serde conversion method signatures
fun getDistinctProtoCollectionFields(entities: Collection<Entity>): Collection<Field> {
  return entities
    .flatMap { it.fields }
    .filter { it.isCollection }
    .sortedBy { it.name.lowerCamel }
  //TODO: distinct here
}

//TODO: make an expression type, and return that
private fun buildFieldDeclarationExpressions(
  fields: Collection<Field>,
  firstFieldNumber: Int = 1,
): List<Expression> =
  fields.mapIndexed { index, field ->

    val repeatedPrefix =
      if (field.protobufConfig.repeated) "repeated "
      else ""

    "${repeatedPrefix}${effectiveType(field)} ${field.name.lowerSnake} = ${index + firstFieldNumber};"

  }.map {
    RawExpression(it)
  }


private fun effectiveProtoSerde(field: Field): Serde =
  if (field.protobufConfig.serde != null) {
    // -- User override is highest priority
    field.protobufConfig.serde

  } else if (field.isCollection) {
    defaultSerdeForCollection(field)

  } else if (requiresProtoSerde(field)) {
    // -- Fallback to jvm serializer
    defaultJVMSerde(field)

  } else {
    Serde.INLINE
  }

private fun effectiveProtoSerdeForTypeParameters(
  field: Field,
): List<Serde> =
  field
    .type
    .typeParameters
    .map {
      if (field.protobufConfig.repeatedItemSerde != null) {
        field.protobufConfig.repeatedItemSerde
      } else {
        Serde.INLINE
      }
    }


/**
 * Assumes we generate serde methods in the template
 */
private fun defaultSerdeForCollection(field: Field): Serde =
  when (field.effectiveBaseType) {
    LIST -> Serde(
      // List<String> -> List<Entity>
      deserializeTemplate = StringFormatTemplate("stringsTo${field.name.upperCamel}List(%s)"),

      // Collection<Entity> -> Collection<String>
      serializeTemplate = StringFormatTemplate("toStrings(%s)"),
    )

    SET -> Serde(
      // List<String> -> Set<Entity>
      deserializeTemplate = StringFormatTemplate("stringsTo${field.name.upperCamel}Set(%s)"),

      // Collection<Entity> -> Collection<String>
      serializeTemplate = StringFormatTemplate("toStrings(%s)"),
    )

    ARRAY -> TODO("handle default serde for Array")
    MAP -> TODO("handle default serde for Map")
    else -> TODO("This method is only for collections")
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

  if (field.protobufConfig.overrideTypeLiteral.isNotBlank()) {
    return field.protobufConfig.overrideTypeLiteral
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
//  CHAR -> TODO()  // probably int32

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
