package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.DefaultValue
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.QuoteType
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Examples:
 * -- SQL (this handles the "DEFAULT" prefix)
 *      DEFAULT 'foo'
 *      DEFAULT 7
 *      DEFAULT NOT NULL
 *      DEFAULT NULL
 *
 * -- Kotlin (Data Class)
 *      ""
 *      "foo"
 *      7
 *      Duration.ofMinutes(5)
 *      listOf()
 *      MY_CONSTANT
 *      MyEnum.ITEM
 *      null
 *      setOf()
 *      true
 *
 * -- Java (POJO Class)
 *      ""
 *      "foo"
 *      7
 *      Collections.emptyList()
 *      Collections.emptySet()
 *      Duration.ofMinutes(5)
 *      MY_CONSTANT
 *      MyEnum.ITEM
 *      null
 *      true
 *
 * -- Golang (struct)
 *      TODO: more here
 */
@Suppress("ReturnCount")
data class DefaultValueExpression(
  private val field: Field,
) : Expression {

  override val expressionName: String = DefaultValueExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig): String {

    val effectiveDefaultValue = field.effectiveDefaultValue(config.targetLanguage)
    if (effectiveDefaultValue.isAbsent) {
      return EmptyExpression.render(config)
    }

    val effectiveBaseType = field.effectiveBaseType(config.targetLanguage)

    return when (config.targetLanguage) {

      GOLANG_1_9,
      -> handleGolang(
        config,
        effectiveBaseType,
        effectiveDefaultValue,
      )

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(
        config,
        effectiveBaseType,
        effectiveDefaultValue,
      )

      KOTLIN_JVM_1_4,
      -> handleKotlin(
        config,
        effectiveBaseType,
        effectiveDefaultValue,
      )

      SQL_DB2,
      SQL_H2,
      SQL_MARIA,
      SQL_MYSQL,
      SQL_ORACLE,
      SQL_POSTGRESQL,
      SQL_DELIGHT,
      SQL_SQLITE,
      -> {

        val output = handleSQL(
          config,
          effectiveBaseType,
          effectiveDefaultValue,
        ).trim()

        if (output.isBlank()) {
          output
        } else {
          "DEFAULT $output"
        }
      }

      else -> TODO("finish rendering defaultValue Expression for $config")
    }
  }

  private fun handleGolang(
    config: RenderConfig,
    effectiveBaseType: BaseFieldType,
    effectiveDefaultValue: DefaultValue,
  ): String {

    if (effectiveDefaultValue.isNullLiteral()) {
      return NullLiteralExpression.render(config)
    }

    TODO("implement default expression for golang")
  }

  private fun handleJava(
    config: RenderConfig,
    effectiveBaseType: BaseFieldType,
    effectiveDefaultValue: DefaultValue,
  ): String {

    if (effectiveDefaultValue.isNullLiteral()) {
      return NullLiteralExpression.render(config)
    }

    if (effectiveDefaultValue.isEmptyCollection()) {
      return when (field.effectiveBaseType(JAVA_08)) {
        LIST -> "Collections.emptyList()"
        MAP -> "Collections.emptyMap()"
        SET -> "Collections.emptySet()"
        else -> TODO("Build Empty java collection for field=$field")
      }
    }

    val v = effectiveDefaultValue.uninterpreted
    checkNotNull(v) { "effectiveDefaultValue.uninterpreted is unexpectedly null" }

    if (!effectiveBaseType.canAssignStringLiteral) {
      return v.toString()
    }

    // Invariant: Stringlike base type
    return if (effectiveDefaultValue.shouldQuote) {
      QuoteType.DOUBLE.wrap(v.toString())

    } else {
      v.toString()
    }
  }

  private fun handleKotlin(
    config: RenderConfig,
    effectiveBaseType: BaseFieldType,
    effectiveDefaultValue: DefaultValue,
  ): String {

    if (effectiveDefaultValue.isNullLiteral()) {
      return NullLiteralExpression.render(config)
    }

    if (effectiveDefaultValue.isEmptyCollection()) {
      return when (effectiveBaseType) {
        LIST -> "listOf()"
        MAP -> "mapOf()"
        SET -> "setOf()"
        else -> TODO("Build Empty kotlin collection for field=$field")
      }
    }

    val v = effectiveDefaultValue.uninterpreted
    checkNotNull(v) { "effectiveDefaultValue.uninterpreted is unexpectedly null" }

    if (!effectiveBaseType.canAssignStringLiteral) {
      return v.toString()
    }

    // Invariant: Stringlike base type
    return if (effectiveDefaultValue.shouldQuote) {
      QuoteType.DOUBLE.wrap(v.toString())

    } else {
      v.toString()
    }
  }

  /** Caller is responsible for "DEFAULT" prefix */
  private fun handleSQL(
    config: RenderConfig,
    effectiveBaseType: BaseFieldType,
    effectiveDefaultValue: DefaultValue,
  ): String {

    if (effectiveDefaultValue.isNullLiteral()) {
      return NullLiteralExpression.render(config)
    }

    check(!effectiveDefaultValue.isEmptyCollection()) {
      "no support for collections directly in database (yet)"
    }

    val v = effectiveDefaultValue.uninterpreted
    checkNotNull(v) { "effectiveDefaultValue.uninterpreted is unexpectedly null" }

    if (!effectiveBaseType.canAssignStringLiteral) {
      return v.toString()
    }

    // Invariant: Stringlike base type
    return if (effectiveDefaultValue.shouldQuote) {
      QuoteType.SINGLE.wrap(v.toString())

    } else {
      v.toString()
    }
  }
}


/**
 * Db2:     TODO
 * H2:      TODO
 * Maria:   TODO
 * MySQL:   TODO
 * Oracle:  TODO
 * Postgres: https://www.postgresql.org/docs/current/sql-createtable.html
 * SQLite:  TODO
 *
 * @return literal for Default value (called "default_expr" in PostgreSQL AST)
 */
//fun rdbmsDefaultValueLiteral(field: Field): String {
//
//  if (field.defaultValue.isAbsent) {
//    return ""
//  }
//
//  val quoteType = quoteTypeForRDBMSLiteral(field.effectiveBaseType(SQL_POSTGRESQL))
//
//  return field
//    .defaultValue
//    .literal(quoteType)
//    ?.toString() ?: "NULL"
//}


//fun defaultValueLiteralForKotlin(field: Field): String {
//  val quoteType = quoteTypeForJVMLiterals(field.type.base)
//
//  return field
//    .defaultValue
//    .literal(quoteType)
//    ?.toString() ?: "null"
//}


//fun defaultValueLiteralForJava(field: Field): String {
//  val quoteType = quoteTypeForJVMLiterals(field.type.base)
//
//  return field
//    .defaultValue
//    .literal(quoteType)
//    ?.toString() ?: "null"
//}
