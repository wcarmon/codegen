@file:JvmName("JDBCCodeUtils")

package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.PreparedStatementBuilderConfig
import com.wcarmon.codegen.model.Serde
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.ast.*
import java.sql.JDBCType


/**
 * Statement termination handled by caller
 *
 * Handles correct column indexing on PreparedStatements
 *
 * @return statements for assigning properties on [java.sql.PreparedStatement]
 */
fun buildPreparedStatementSetters(
  cfg: PreparedStatementBuilderConfig,
  fields: List<Field>,
  firstIndex: Int = 1,
): List<Expression> {
  require(firstIndex > 0) {
    "firstIndex must be positive: $firstIndex"
  }

  return fields.mapIndexed { columnIndex, currentField ->
    buildPreparedStatementSetter(
      cfg = cfg,
      columnIndex = firstIndex + columnIndex,
      field = currentField,
    )
  }
}

/**
 * @return statement for assigning field on PreparedStatement
 *   eg. "ps.setString(7, foo.toString())"
 */
fun buildPreparedStatementSetter(
  cfg: PreparedStatementBuilderConfig,
  columnIndex: Int,
  field: Field,
): Expression {

  require(columnIndex > 0) {
    "columnIndex must be positive: $columnIndex"
  }

  val fieldReadExpression = FieldReadExpression(
    assertNonNull = field.type.nullable && cfg.allowFieldNonNullAssertion,
    fieldName = field.name,
    fieldReadPrefix = cfg.fieldReadPrefix,
    overrideFieldReadStyle = cfg.fieldReadStyle,
  )

  val expressionForNonNull =
    PreparedStatementNonNullSetterExpression(
      columnIndex = columnIndex,
      // Wrap the field read in a Serde
      newValueExpression = SerdeReadExpression(
        fieldReadExpression = fieldReadExpression,
        serdeTemplate = effectiveJDBCSerde(field).serializeTemplate,
      ),
      preparedStatementIdentifier = cfg.preparedStatementIdentifier,
      setterMethod = defaultPreparedStatementSetterMethod(field.effectiveBaseType),
    )

  if (!field.type.nullable) {
    return expressionForNonNull
  }

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
) =
  // Wraps ResultSetGetter in the Serde
  SerdeReadExpression(
    fieldReadExpression = ResultSetGetterExpression(
      fieldName = field.name,
      getterMethod = defaultResultSetGetterMethod(field.effectiveBaseType),
      resultSetIdentifier = resultSetIdentifier,
    ),

    serdeTemplate = effectiveJDBCSerde(field).forMode(DESERIALIZE),
  )

/**
 * @return the most appropriate Serde for JDBC
 */
private fun effectiveJDBCSerde(field: Field): Serde =
  if (field.rdbms?.serde != null) {
    // -- User override is highest priority
    field.rdbms.serde

  } else if (requiresJDBCSerde(field)) {
    // -- Fallback to jvm serializer
    defaultJVMSerde(field)

  } else {
    Serde.INLINE
  }


/**
 * @return true when Type is not trivially mapped to JDBC getter/setter
 */
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
private fun defaultPreparedStatementSetterMethod(base: BaseFieldType): MethodName =
  defaultResultSetGetterMethod(base)
    .value
    .replaceFirst("get", "set")
    .let { MethodName(it) }


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

/**
 * @return getter method name declared on [java.sql.ResultSet]
 */
private fun defaultResultSetGetterMethod(base: BaseFieldType): MethodName = when (base) {
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
