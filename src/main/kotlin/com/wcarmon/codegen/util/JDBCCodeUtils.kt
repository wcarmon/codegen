@file:JvmName("JDBCCodeUtils")

package com.wcarmon.codegen.util

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
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
  firstIndex: JDBCColumnIndex = JDBCColumnIndex.FIRST,
): Collection<Expression> =
  fields.mapIndexed { columnIndex, currentField ->

    val fieldReadExpression = FieldReadExpression(
      assertNonNull = currentField.type.nullable && cfg.allowFieldNonNullAssertion,
      fieldName = currentField.name,
      fieldOwner = cfg.fieldOwner,
      overrideFieldReadStyle = cfg.fieldReadMode,
    )

    PreparedStatementSetExpression(
      columnIndex = JDBCColumnIndex(firstIndex.value + columnIndex),
      columnType = jdbcType(currentField.effectiveBaseType),
      fieldReadExpression = WrapWithSerdeExpression(
        serde = effectiveJDBCSerde(currentField),
        serdeMode = SERIALIZE,
        wrapped = fieldReadExpression,
      ),
      field = currentField,
      preparedStatementIdentifierExpression = RawExpression("ps"),
      setterMethod = defaultPreparedStatementSetterMethod(currentField.effectiveBaseType),
    )
  }

/**
 * @return the most appropriate Serde for JDBC
 */
private fun effectiveJDBCSerde(field: Field): Serde =
  if (field.rdbms.serde != null) {
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
private fun defaultPreparedStatementSetterMethod(base: BaseFieldType): Name =
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
private fun defaultResultSetGetterMethod(base: BaseFieldType): Name = when (base) {
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
  .let { Name(it) }
