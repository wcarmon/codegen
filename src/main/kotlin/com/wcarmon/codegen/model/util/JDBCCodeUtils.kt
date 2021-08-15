@file:JvmName("JDBCCodeUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.FieldReadStyle
import com.wcarmon.codegen.model.FieldReadStyle.DIRECT
import com.wcarmon.codegen.model.FieldReadStyle.GETTER

/**
 * Statement termination handled by caller
 *
 * This handles correct column indexing on PreparedStatements
 *
 * @param firstIndex TODO
 * @param fields
 * @param preparedStatementIdentifier TODO
 *
 * @return statements for assigning properties on [java.sql.PreparedStatement]
 */
fun buildPreparedStatementSetterStatements(
  fieldReadPrefix: String = "",
  fieldReadStyle: FieldReadStyle,
  fields: List<Field>,
  firstIndex: Int = 1,
  preparedStatementIdentifier: String = "ps",
): List<String> =
  fields.mapIndexed { index, field ->
    buildPreparedStatementSetterStatement(
      columnIndex = firstIndex + index,
      field = field,
      fieldReadPrefix = fieldReadPrefix,
      fieldReadStyle = fieldReadStyle,
      preparedStatementIdentifier = preparedStatementIdentifier,
    )
  }

/**
 * @param columnIndex
 * @param field
 * @param fieldReadPrefix  scope for reading field.  eg. "myEntity."
 * @param fieldReadStyle
 * @param preparedStatementIdentifier prefix for PreparedStatement setter
 *
 * @return statement for assigning field on PreparedStatement
 *   eg. "ps.setString(7, foo.toString())"
 */
fun buildPreparedStatementSetterStatement(
  columnIndex: Int,
  field: Field,
  fieldReadPrefix: String = "",
  fieldReadStyle: FieldReadStyle,
  preparedStatementIdentifier: String = "ps",
): String {
  require(fieldReadPrefix.trim() == fieldReadPrefix) {
    "fieldReadPrefix must be trimmed: $fieldReadPrefix"
  }

  //TODO: handle when you might call .toString on a null field (or null output from getter)
  val serializedFieldExpression = jdbcSerializedFieldExpression(
    field = field,
    fieldReadStyle = fieldReadStyle,
    fieldReadPrefix = fieldReadPrefix,
  )

  // eg. "setLong"
  val setterMethodName =
    getDefaultPreparedStatementSetter(field.effectiveBaseType)

  if (field.type.nullable) {
//    setterMethodName = "setNull"
    //TODO: use ps.setNull when the value is null on a nullable column
    //    you will get NPE on ps.setInt if you skip this
  }

  return "${preparedStatementIdentifier}.${setterMethodName}($columnIndex, ${serializedFieldExpression})"
}

/**
 * @param field
 * @param fieldReadStyle
 * @param fieldReadPrefix  TODO
 *
 * @return an expression which converts [Field] to a value JDBC will accept
 * eg. "myEntity.myField.toString()"
 */
fun jdbcSerializedFieldExpression(
  field: Field,
  fieldReadStyle: FieldReadStyle,
  fieldReadPrefix: String = "",
): String {
  check(fieldReadPrefix.trim() == fieldReadPrefix) {
    "fieldReadPrefix must be trimmed: $fieldReadPrefix"
  }

  //eg. "%s.toString()"
  //eg. "objectWriter.writeValueAsString(%s)"
  val fieldSerializeExpressionTemplate =
    if (shouldUseCustomJDBCSerde(field)) {
      getJDBCSerializeTemplate(field)

    } else {
      "%s"
    }

  val fieldReadExpression = when (fieldReadStyle) {
    DIRECT -> field.name.lowerCamel
    GETTER -> "get${field.name.upperCamel}()"
  }

  return String.format(
    fieldSerializeExpressionTemplate,
    fieldReadPrefix + fieldReadExpression
  )
}

/**
 * Builds an expression to retrieve one [Field] from DB
 *
 * Override default behavior by setting Field.rdbms.deserializeTemplate
 *
 * @param field to retrieve from database
 *
 * @return an expression,
 *  expression uses a resultSet getter method,
 *  expression returns a value with type matching [field]
 */
fun buildResultSetGetterExpression(
  field: Field,
  resultSetIdentifier: String = "rs",
): String {

  // eg. "getLong"
  val getterMethodName =
    getDefaultResultSetGetter(field.effectiveBaseType)

  // eg. rs.getString("foo")
  val fieldValueExpression =
    """${resultSetIdentifier}.${getterMethodName}("${field.name.lowerSnake}")"""

  // Expression to build field from String
  val deserializeExpressionTemplate =
    if (shouldUseCustomJDBCSerde(field)) {
      getJDBCDeserializeTemplate(field)

    } else if (field.isCollection) {
      // Use [com.fasterxml.jackson.core.type.TypeReference] for type-safe deserializing
      "to${field.type.rawTypeLiteral}(%s, ${field.name.upperSnake}_TYPE_REF)"

    } else {
      "%s"
    }

  // -- Expand the template
  return String.format(
    deserializeExpressionTemplate,
    fieldValueExpression)
}

//TODO: document me
private fun shouldUseCustomJDBCSerde(field: Field): Boolean =
  field.hasCustomJDBCSerde
      || field.effectiveBaseType in setOf(PATH, URI, URL)
      || field.effectiveBaseType.isTemporal
      || field.isCollection
      || field.type.enumType

/**
 * See [java.sql.ResultSet] (getter names match setters on [java.sql.PreparedStatement])
 *
 * @return setter method name declared on [java.sql.PreparedStatement]
 */
private fun getDefaultPreparedStatementSetter(base: BaseFieldType) =
  getDefaultResultSetGetter(base)
    .replaceFirst("get", "set")


/**
 * @return getter method name declared on [java.sql.ResultSet]
 */
private fun getDefaultResultSetGetter(base: BaseFieldType) = when (base) {
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
 * @return an expression with placeholder (%s)
 * TODO: more info
 */
private fun getJDBCDeserializeTemplate(field: Field) =
  if (field.rdbms != null &&
    field.rdbms.deserializeTemplate.isNotBlank()
  ) {
    // -- User override is highest priority
    field.rdbms.deserializeTemplate


  } else if (shouldUseCustomJDBCSerde(field)) {
    // -- Fallback to java serializer if applicable
    defaultJavaDeserializeTemplate(field.type)

  } else {
    // -- Direct deserialization (no wrapper/parser methods)
    "%s"
  }

/**
 * @return an expression with placeholder (%s)
 * TODO: more info
 */
private fun getJDBCSerializeTemplate(field: Field): String =
  if (field.rdbms != null &&
    field.rdbms.serializeTemplate.isNotBlank()
  ) {
    // -- User override is highest priority
    field.rdbms.serializeTemplate


  } else if (shouldUseCustomJDBCSerde(field)) {
    // -- Fallback to java serializer if applicable
    defaultJavaSerializeTemplate(field.type)

  } else {
    // -- Direct deserialization (no wrapper/parser methods)
    "%s"
  }
