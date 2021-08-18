@file:JvmName("JDBCCodeUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.ast.*
import java.sql.JDBCType


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
fun buildPreparedStatementSetters(
  cfg: PreparedStatementBuilderConfig,
  fields: List<Field>,

  firstIndex: Int = 1,
): List<Expression> =
  fields.mapIndexed { index, field ->
    buildPreparedStatementSetter(
      cfg = cfg,
      columnIndex = firstIndex + index,
      field = field,
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
fun buildPreparedStatementSetter(
  cfg: PreparedStatementBuilderConfig,
  columnIndex: Int,
  field: Field,
): Expression {

  val expressionForNonNull =
    PreparedStatementNonNullSetterExpression(
      columnIndex = columnIndex,
      newValueExpression = jdbcFieldReadExpression(
        assertNonNull = field.type.nullable && cfg.allowFieldNonNullAssertion,
        field = field,
        fieldReadPrefix = cfg.fieldReadPrefix,
        fieldReadStyle = cfg.fieldReadStyle,
        targetLanguage = cfg.targetLanguage,
      ),
      preparedStatementIdentifier = cfg.preparedStatementIdentifier,
      setter = defaultPreparedStatementSetter(field.effectiveBaseType),
    )

  if (field.type.nullable) {

    return ConditionalExpression(
      condition = NullComparisonExpression(
        FieldReadExpression(
          fieldName = field.name,
          fieldReadPrefix = cfg.fieldReadPrefix,
          overrideFieldReadStyle = cfg.fieldReadStyle,
        )),
      expressionForTrue = PreparedStatementNullSetterExpression(
        columnIndex = columnIndex,
        columnType = jdbcType(field.effectiveBaseType),
        preparedStatementIdentifier = cfg.preparedStatementIdentifier,
      ),
      expressionForFalse = expressionForNonNull,
    )
  }

  return expressionForNonNull
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
  targetLanguage: TargetLanguage,
): Expression { //TODO: is there a more granular return type I should use

  val fieldReadExpression = ResultSetGetterExpression(
    fieldName = field.name,
    getter = defaultResultSetGetter(field.effectiveBaseType),
    resultSetIdentifier = resultSetIdentifier,
  ).serialize(targetLanguage, false)

  // -- Wrap the field read expression in the serde expression
  return jdbcSerde(field)
    .deserializeTemplate
    .expand(
      fieldReadExpression
    )
}

/**
 *
 * handles type conversion using Serde
 *
 * @return an expression which converts [Field] to a value JDBC will accept
 * eg. "myEntity.myField.toString()"
 */
private fun jdbcFieldReadExpression(
  //TODO: too many args
  assertNonNull: Boolean = false,
  field: Field,
  fieldReadPrefix: String = "",
  fieldReadStyle: FieldReadStyle,
  targetLanguage: TargetLanguage,
  terminate: Boolean = false,
): Expression {

  //TODO: handle when you might call .toString on a null field (or null output from getter)

  check(fieldReadPrefix.trim() == fieldReadPrefix) {
    "fieldReadPrefix must be trimmed: $fieldReadPrefix"
  }

  val fieldReadExpression = FieldReadExpression(
    assertNonNull = assertNonNull,
    fieldName = field.name,
    fieldReadPrefix = fieldReadPrefix,
    overrideFieldReadStyle = fieldReadStyle,
  )

  return jdbcSerde(field)
    .serializeTemplate
    .expand(
      fieldReadExpression.serialize(
        targetLanguage,
        terminate))
}


private fun jdbcSerde(field: Field): Serde =
  if (field.rdbms != null &&
    field.rdbms.serde != null
  ) {
    // -- User override is highest priority
    field.rdbms.serde

  } else if (field.isCollection) {
    // Use [com.fasterxml.jackson.core.type.TypeReference] for type-safe deserializing
    Serde(
      deserializeTemplate = ExpressionTemplate(
        "to${field.type.rawTypeLiteral}(%s, ${field.name.upperSnake}_TYPE_REF)"),

      //TODO: Kotlin:  myCollection.map { it.label }.sortedBy { it }.joinToString(MY_COLLECTION_DELIM),
      serializeTemplate = TODO("fix this "),
    )

  } else if (requiresJDBCSerde(field)) {
    // -- Fallback to java serializer
    defaultJavaSerde(field)

  } else {
    Serde.INLINE
  }


/**
 * See [java.sql.Types]
 * See [java.sql.JDBCType]
 *
 * Essential for [java.sql.PreparedStatement.setNull]
 */
private fun jdbcType(base: BaseFieldType): JDBCType = when (base) {
  BOOLEAN -> JDBCType.BOOLEAN
  FLOAT_32 -> JDBCType.FLOAT
  FLOAT_64 -> JDBCType.DOUBLE
  FLOAT_BIG -> JDBCType.DECIMAL
  INT_128 -> JDBCType.BIGINT
  INT_16 -> JDBCType.SMALLINT
  INT_32 -> JDBCType.INTEGER
  INT_64 -> JDBCType.BIGINT
  INT_8 -> JDBCType.TINYINT
  INT_BIG -> JDBCType.NUMERIC //TODO: should this be BIGINT?
  YEAR -> JDBCType.INTEGER
  ZONE_OFFSET -> JDBCType.INTEGER

  ARRAY,
  CHAR,
  DURATION,
  LIST,
  MAP,
  MONTH_DAY,
  PATH,
  PERIOD,
  SET,
  STRING,
  URI,
  URL,
  USER_DEFINED,
  UTC_INSTANT,
  UTC_TIME,
  UUID,
  YEAR_MONTH,
  ZONE_AGNOSTIC_DATE,
  ZONE_AGNOSTIC_TIME,
  ZONED_DATE_TIME,
  -> JDBCType.VARCHAR

//  TODO: handle Types.BLOB
//  TODO: handle Types.CLOB (allow override in *.entity.json)
}


/** Some types use standard parse & toString methods */
private fun requiresJDBCSerde(field: Field): Boolean =
  field.effectiveBaseType in setOf(PATH, URI, URL)
      || field.effectiveBaseType.isTemporal
      || field.isCollection
      || field.type.enumType


/**
 * See [java.sql.ResultSet] (getter names match setters on [java.sql.PreparedStatement])
 *
 * eg. setLong
 *
 * @return setter method name declared on [java.sql.PreparedStatement]
 */
private fun defaultPreparedStatementSetter(base: BaseFieldType): MethodName =
  defaultResultSetGetter(base)
    .value
    .replaceFirst("get", "set")
    .let { MethodName(it) }

/**
 * @return getter method name declared on [java.sql.ResultSet]
 */
private fun defaultResultSetGetter(base: BaseFieldType): MethodName = when (base) {
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
  .let { MethodName(it) }
