@file:JvmName("ProtoBufFieldUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*


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


//TODO: improve documentation
// Get the collection fields, avoid duplicate serde conversion method signatures
fun getDistinctProtoCollectionFields(entities: Collection<Entity>): Collection<Field> {
  return entities
    .flatMap { it.fields }
    .filter { it.isCollection }
    .sortedBy { it.name.lowerCamel }
  //TODO: distinct here
}

fun effectiveProtobufType(field: Field): String {

  if (field.protobufConfig.overrideTypeLiteral.isNotBlank()) {
    return field.protobufConfig.overrideTypeLiteral
  }

  return protobufTypeLiteral(field.type)
}


fun effectiveProtoSerde(field: Field): Serde =
  if (field.protobufConfig.overrideSerde != Serde.INLINE) {
    // -- User override is highest priority
    field.protobufConfig.overrideSerde

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
      field.protobufConfig.overrideRepeatedItemSerde ?: Serde.INLINE
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
