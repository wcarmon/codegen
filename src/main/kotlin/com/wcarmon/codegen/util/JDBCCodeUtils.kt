@file:JvmName("JDBCCodeUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage.SQL_POSTGRESQL
import java.sql.JDBCType


/**
 * Statement termination handled by caller
 *
 * Handles correct column indexing on PreparedStatements
 *
 * @return statements for assigning properties on [java.sql.PreparedStatement]
 */
fun buildPreparedStatementSetters(
  psConfig: PreparedStatementBuilderConfig,
  fields: List<Field>,
  firstIndex: JDBCColumnIndex = JDBCColumnIndex.FIRST,
): Collection<Expression> =

  fields.mapIndexed { columnIndex, currentField ->

    buildPreparedStatementSetter(
      columnIndex = JDBCColumnIndex(firstIndex.value + columnIndex),
      field = currentField,
      psConfig = psConfig,
    )
  }

/**
 * Handles null able fields, See [PreparedStatementSetExpression]
 * Wraps read in serde, See [WrapWithSerdeExpression]
 * Handles statement termination (when applicable)
 *
 * @return [PreparedStatementSetExpression]
 */
fun buildPreparedStatementSetter(
  columnIndex: JDBCColumnIndex,
  field: Field,
  psConfig: PreparedStatementBuilderConfig,
): PreparedStatementSetExpression {

  val fieldReadExpression = FieldReadExpression(
    assertNonNull = false,
    fieldName = field.name,
    fieldOwner = psConfig.fieldOwner,
    overrideFieldReadMode = psConfig.fieldReadMode,
  )

  val nullTestExpression = NullComparisonExpression(
    compareToMe = fieldReadExpression
  )

  val wrappedFieldRead = WrapWithSerdeExpression(
    serde = effectiveJDBCSerde(field),
    serdeMode = SERIALIZE,
    wrapped = fieldReadExpression,
  )

  val baseType = field.effectiveBaseType(SQL_POSTGRESQL)

  return PreparedStatementSetExpression(
    columnIndex = columnIndex,
    columnType = jdbcType(baseType),
    fieldReadExpression = wrappedFieldRead,
    field = field,
    nullTestExpression = nullTestExpression,
    preparedStatementIdentifierExpression = psConfig.preparedStatementIdentifierExpression,
    setterMethod = defaultPreparedStatementSetterMethod(baseType),
  )
}

/**
 * @return the most appropriate Serde for JDBC
 */
fun effectiveJDBCSerde(field: Field): Serde =
  if (field.rdbmsConfig.overrideSerde != Serde.INLINE) {
    // -- User override is highest priority
    field.rdbmsConfig.overrideSerde

  } else if (requiresJDBCSerde(field)) {
    // -- Fallback to jvm serializer
    defaultJVMSerde(field)

  } else {
    Serde.INLINE
  }


/**
 * @return true when Type is not trivially mapped to JDBC getter/setter
 */
private fun requiresJDBCSerde(field: Field): Boolean {
  val baseType = field.effectiveBaseType(SQL_POSTGRESQL)

  return (baseType in setOf(PATH, URI, URL)
      || baseType.isTemporal
      || baseType.isCollection
      || field.type.enumType)
}


/**
 * See [java.sql.ResultSet] (getter names match setters on [java.sql.PreparedStatement])
 *
 * eg. setLong
 *
 * @return setter method name declared on [java.sql.PreparedStatement]
 */
fun defaultPreparedStatementSetterMethod(base: BaseFieldType): Name =
  defaultResultSetGetterMethod(base)
    .lowerCamel
    .replaceFirst("get", "set")
    .let { Name(it) }


/**
 * See [java.sql.Types]
 * See [java.sql.JDBCType]
 *
 * Essential for [java.sql.PreparedStatement.setNull]
 */
fun jdbcType(base: BaseFieldType): JDBCType = when (base) {
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
  COLOR,
  DURATION,
  EMAIL,
  LIST,
  MAP,
  MONTH_DAY,
  PATH,
  PERIOD,
  PHONE_NUMBER,
  SET,
  STRING,
  URI,
  URL,
  USER_DEFINED,
  UTC_INSTANT,
  UTC_TIME,
  UUID,
  WEEK_OF_YEAR,
  YEAR_MONTH,
  ZONE_AGNOSTIC_DATE,
  ZONE_AGNOSTIC_DATE_TIME,
  ZONE_AGNOSTIC_TIME,
  ZONED_DATE_TIME,
  -> JDBCType.VARCHAR

//  TODO: handle Types.BLOB
//  TODO: handle Types.CLOB (allow override in *.entity.json)
}

/**
 * @return getter method name declared on [java.sql.ResultSet]
 */
fun defaultResultSetGetterMethod(base: BaseFieldType): Name = when (base) {
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
  COLOR,
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
  .let { Name(it) }
