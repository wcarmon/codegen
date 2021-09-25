@file:JvmName("ProtoBufFieldUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Serde.Companion.INLINE
import com.wcarmon.codegen.model.TargetLanguage.PROTOCOL_BUFFERS_3


/**
 * @return Name of setter method on builder
 */
fun protoSetterMethodName(field: Field): Name =
  if (field.effectiveBaseType(PROTOCOL_BUFFERS_3).isCollection) {
    "addAll"
  } else {
    "set"
  }
    .let { prefix ->
      Name("$prefix${field.name.upperCamel}")
    }


/**
 * Method name on a proto to retrieve a field
 */
fun protoGetterMethodName(field: Field): Name =
  if (field.effectiveBaseType(PROTOCOL_BUFFERS_3).isCollection) {
    "List"
  } else {
    ""
  }
    .let { suffix ->
      Name("get${field.name.upperCamel}$suffix")
    }


//TODO: replace with field::typeLiteral
//fun effectiveProtobufType(field: Field): String {


fun effectiveProtoSerde(field: Field): Serde =
  if (field.protobufConfig.overrideSerde != INLINE) {
    // -- User override is highest priority
    field.protobufConfig.overrideSerde

  } else if (field.effectiveBaseType(PROTOCOL_BUFFERS_3).isCollection) {
    defaultSerdeForCollection(field)

  } else if (requiresProtoSerde(field)) {
    // -- Fallback to jvm serializer
    defaultJVMSerde(field)

  } else {
    INLINE
  }


fun effectiveProtoSerdesForTypeParameters(
  field: Field,
): List<Serde> =
  field
    .protobufConfig
    .typeParameters
    .map {
      field.protobufConfig.overrideRepeatedItemSerde ?: INLINE
    }


/**
 * Assumes we generate serde methods in the template
 */
private fun defaultSerdeForCollection(field: Field): Serde =
  when (field.effectiveBaseType(PROTOCOL_BUFFERS_3)) {
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
private fun requiresProtoSerde(field: Field): Boolean {

  val baseType = field.effectiveBaseType(PROTOCOL_BUFFERS_3)

  return (baseType in setOf(PATH, URI, URL)
      || baseType.isTemporal
      || baseType.isCollection
      || field.type.enumType)
}

/**
 * See https://developers.google.com/protocol-buffers/docs/proto3#scalar
 */
fun protobufTypeLiteral(
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
