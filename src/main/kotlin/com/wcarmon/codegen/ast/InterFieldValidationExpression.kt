package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.InterFieldValidation
import com.wcarmon.codegen.model.InterFieldValidationType.*
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Builds 1 or more expressions for two fields
 */
data class InterFieldValidationExpression(
  val entity: Entity,
  val validationConfig: InterFieldValidation,
  val validationSeparator: String = "\n",
) : Expression {

  override val expressionName: String = InterFieldValidationExpression::class.java.simpleName

  private val field0 = entity.fieldForName(validationConfig.fieldName0)
    ?: throw RuntimeException("Cannot find field: field.name=${validationConfig.fieldName0}, entity=${entity.name.upperCamel}")

  private val field1 = entity.fieldForName(validationConfig.fieldName1)
    ?: throw RuntimeException("Cannot find field: field.name=${validationConfig.fieldName1}, entity=${entity.name.upperCamel}")


  override fun renderWithoutDebugComments(config: RenderConfig): String =
    when (config.targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4 -> handleKotlin(config)

      SQL_POSTGRESQL
      -> handlePostgreSQL(config)

      else -> TODO("handle InterFieldValidation for ${config.targetLanguage}")
    }.trimEnd()

  private fun handleJava(config: RenderConfig): String {
    val name0 = field0.name.lowerCamel
    val name1 = field1.name.lowerCamel
    val output = mutableListOf<String>()

    when (validationConfig.type) {
      BEFORE -> {
        output += """
        |Preconditions.checkArgument(
        |  $name0.isBefore($name1),
        |  "'$name0' must be before '$name1': $name0=" + $name0 + ", $name1=" + $name1);
        """.trimMargin()
      }

      NOT_BEFORE -> {
        output += """
        |Preconditions.checkArgument(
        |  !$name0.isBefore($name1),
        |  "'$name0' must not be before '$name1': $name0=" + $name0 + ", $name1=" + $name1);
        """.trimMargin()
      }

      NOT_EQUAL -> {
        //TODO: handle != vs .equals based on field type (primitive, enum)
        output += """
        |Preconditions.checkArgument(
        |  !$name0.equals($name1),
        |  "'$name0' must not equal '$name1': $name0=" + $name0 + ", $name1=" + $name1);
        """.trimMargin()
      }

      LESS_THAN -> {
        output += """
        |Preconditions.checkArgument(
        |  $name0 < $name1,
        |  "'$name0' must be less than '$name1': $name0=" + $name0 + ", $name1=" + $name1);
        """.trimMargin()
      }

      LESS_OR_EQUAL_TO -> {
        output += """
        |Preconditions.checkArgument(
        |  $name0 <= $name1,
        |  "'$name0' must not exceed '$name1': $name0=" + $name0 + ", $name1=" + $name1);
        """.trimMargin()
      }
    }

    return output.joinToString(
      separator = validationSeparator
    ) {
      "${config.lineIndentation}$it"
    }
  }

  private fun handleKotlin(config: RenderConfig): String {

    val name0 = field0.name.lowerCamel
    val name1 = field1.name.lowerCamel
    val output = mutableListOf<String>()

    when (validationConfig.type) {
      BEFORE -> {
        output += """
        |require($name0.isBefore($name1)) {
        |   "'$name0' must not be before '$name1': $name0=$${name0}, ${name1}=$${name1}"
        |}
        """.trimMargin()
      }

      NOT_BEFORE -> {
        output += """
        |require(!$name0.isBefore($name1)) {
        |   "'$name0' must not be before '$name1': $name0=$${name0}, ${name1}=$${name1}"
        |}
        """.trimMargin()
      }

      NOT_EQUAL -> {
        output += """
          |require($name0 != $name1) {
          |  "'$name0' must be different from '$name1': $name0=$${name0}, ${name1}=$${name1}"
          |}
        """.trimMargin()
      }

      LESS_THAN -> {
        output += """
          |require($name0 < $name1) {
          |  "'$name0' must be less than '$name1': $name0=$${name0}, ${name1}=$${name1}"
          |}
        """.trimMargin()
      }

      LESS_OR_EQUAL_TO -> {
        output += """
          |require($name0 <= $name1) {
          |  "'$name0' must not exceed '$name1': $name0=$${name0}, ${name1}=$${name1}"
          |}
        """.trimMargin()
      }
    }

    return output.joinToString(
      separator = validationSeparator
    ) {
      "${config.lineIndentation}$it"
    }
  }

  private fun handlePostgreSQL(config: RenderConfig): String {
    val name0 = field0.name.lowerSnake
    val name1 = field1.name.lowerSnake

    //TODO: use config.lineIndentation
    return when (validationConfig.type) {
      BEFORE -> """CHECK("$name0" < "$name1")"""
      NOT_BEFORE -> """CHECK("$name0" >= "$name1")"""
      LESS_OR_EQUAL_TO -> """CHECK("$name0" <= "$name1")"""
      LESS_THAN -> """CHECK("$name0" < "$name1")"""
      NOT_EQUAL -> """CHECK("$name0" <> "$name1")"""
    }
  }
}
