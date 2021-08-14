@file:JvmName("JDBCCodeUtils")

package com.wcarmon.codegen.model.utils

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.util.defaultJavaDeserializerTemplate


/**
 * See https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/ResultSet.html
 *
 * @return setter method literal declared on [java.sql.ResultSet]
 */
fun jdbcSetter(base: BaseFieldType) =
  jdbcGetter(base).replaceFirst("get", "set")

/**
 * See https://docs.oracle.com/en/java/javase/11/docs/api/java.sql/java/sql/ResultSet.html
 *
 * @return getter method literal declared on [java.sql.ResultSet]
 */
fun jdbcGetter(base: BaseFieldType) = when (base) {
  BOOLEAN -> "getBoolean"
  FLOAT_32 -> "getFloat"
  FLOAT_64 -> "getDouble"
  FLOAT_BIG -> "getBigDecimal"
  INT_16 -> "getShort"
  INT_32 -> "getInt"
  INT_64 -> "getLong"
  INT_8 -> "getByte"
  URL -> "getURL"
  YEAR -> "getInt"
  ZONE_OFFSET -> "getInt"

  ARRAY,
  DURATION,
  LIST,
  MAP,
  MONTH_DAY,
  PATH,
  PERIOD,
  SET,
  STRING,
  URI,
  USER_DEFINED,
  UTC_INSTANT,
  UTC_TIME,
  UUID,
  YEAR_MONTH,
  ZONE_AGNOSTIC_DATE,
  ZONE_AGNOSTIC_TIME,
  ZONED_DATE_TIME,
  -> "getString"

  // TODO: CHAR, // 16-bit Unicode character
  // TODO: INT_128
  // TODO: INT_BIG

  else -> TODO("Add jdbc getter for $base")
}

/**
 * @param field
 * @param fieldValueExpression - replacement, fills the placeholder in the template
 */
fun expandJDBCDeserializeTemplate(
  field: Field,
  fieldValueExpression: String,
): String {

  check(shouldUseJDBCDeserializer(field)) {
    "only invoke when we should use JDBC deserializer"
  }

  if (field.rdbms != null &&
    field.rdbms.deserializerTemplate.isNotBlank()
  ) {
    return String.format(
      field.rdbms.deserializerTemplate,
      fieldValueExpression)
  }

  if (field.type.base.isTemporal
    || field.type.base in setOf(PATH, URI, URL)
    || field.type.enumType
  ) {
    return String.format(
      defaultJavaDeserializerTemplate(field.type),
      fieldValueExpression)
  }

  TODO("handle expanding jdbc deserialize template: $field")
}

//TODO: document me
fun shouldUseJDBCDeserializer(field: Field): Boolean =
  field.hasCustomJDBCSerde
      || field.type.base in setOf(PATH, URI, URL)
      || field.type.base.isTemporal
      || field.type.enumType

/**
 * statement termination handled by caller
 *
 * @return statements for assigning properties on [java.sql.PreparedStatement]
 */
fun buildPreparedStatementSetterStatements(
  fields: List<Field>,
  firstIndex: Int = 1,
  preparedStatementIdentifier: String = "ps",
) =
  fields.mapIndexed { index, field ->
    "${preparedStatementIdentifier}.${jdbcSetter(field.type.base)}(" +
        "${firstIndex + index}, " +
        //TODO: need to serialize this
        "${field.name.lowerCamel})"
  }

/**
 * @param TODO
 *
 * @return an expression,
 *  expression uses a resultSet getter method,
 *  expression returns a value with type matching [field]
 */
fun buildResultSetGetterExpression(field: Field): String {

  // -- Custom Serde
  if (shouldUseJDBCDeserializer(field)) {

    val resultSetGetterMethod =
      if (field.rdbms != null
        && field.rdbms.overrideTypeLiteral.isNotBlank()
      ) {
        jdbcGetter(
          BaseFieldType.parse(
            field.rdbms.overrideTypeLiteral))
      } else {
        "getString"
      }

    val fieldValueExpression = """rs.${resultSetGetterMethod}("${field.name.lowerSnake}")"""

    return expandJDBCDeserializeTemplate(field, fieldValueExpression)
  }

  // -- Collections (JSON via Jackson)
  // Assumes a [com.fasterxml.jackson.core.type.TypeReference] exists
  // See RowMappers for example
  if (field.isCollection) {
    val fieldValueExpression = """rs.getString("${field.name.lowerSnake}")"""

    return "to${field.type.rawTypeLiteral}(" +
        "$fieldValueExpression, " +
        "${field.name.upperSnake}_TYPE_REF)"
  }

  // -- Direct assignment
  return """rs.${jdbcGetter(field.type.base)}("${field.name.lowerSnake}")"""
}
