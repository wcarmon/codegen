@file:JvmName("ProtobufFieldUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.TargetLanguage.*


/**
 * @return Name of setter method on builder
 */
fun protobufSetterMethodName(field: Field): Name =
  if (field.effectiveBaseType(PROTO_BUF_3).isCollection) {
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
fun protobufGetterMethodName(field: Field): Name =
  if (field.effectiveBaseType(PROTO_BUF_3).isCollection) {
    "List"
  } else {
    ""
  }
    .let { suffix ->
      Name("get${field.name.upperCamel}$suffix")
    }


/**
 * Assumes we generate serde methods in the template
 */
fun defaultSerdeForCollection(field: Field, targetLanguage: TargetLanguage): Serde =
  when (targetLanguage) {
    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> defaultJavaSerdeForCollection(field, targetLanguage)

    KOTLIN_JVM_1_4,
    -> defaultKotlinSerdeForCollection(field, targetLanguage)

    else -> TODO("handle protobuf serde for targetLanguage=$targetLanguage")
  }


/**
 * @return true when Type is not trivially mapped to Proto field type
 */
fun requiresProtobufSerde(field: Field): Boolean {

  val baseType = field.effectiveBaseType(PROTO_BUF_3)

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

  COLOR,
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

  USER_DEFINED -> TODO("Add a type override in field.protobuf.overrideEffectiveType: $type")

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

private fun defaultKotlinSerdeForCollection(field: Field, targetLanguage: TargetLanguage) =
//TODO: for kotlin, use fluent collection API
  when (field.effectiveBaseType(targetLanguage)) {
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

    else -> TODO("defaultSerdeForCollection method is only for collections: field=$field")
  }

private fun defaultJavaSerdeForCollection(field: Field, targetLanguage: TargetLanguage) =
  when (field.effectiveBaseType(targetLanguage)) {
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

    else -> TODO("defaultSerdeForCollection method is only for collections: field=$field")
  }
