package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.BaseFieldType.PATH
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.FieldValidation
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.util.isPrimitive

/**
 * Builds 1 or more expressions for the field.
 * Validations are performed in a safe order.
 */
data class FieldValidationExpressions(
  val field: Field,
  val validationConfig: FieldValidation,
  val validationSeparator: String = "\n",
) : Expression {

  override val expressionName: String = FieldValidationExpressions::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig): String =
    when (config.targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4 -> handleKotlin(config)

      else -> TODO()
    }.trimEnd()

  //TODO: fix indentation on multi-line validations
  private fun handleJava(config: RenderConfig): String {

    val baseType = field.effectiveBaseType(JAVA_08)
    val output = mutableListOf<String>()

    // GOTCHA: Java type system doesn't enforce null safety, so we do it manually
    if (!isPrimitive(field) && !field.type.nullable) {
      output +=
        """Objects.requireNonNull(${field.name.lowerCamel}, "'${field.name.lowerCamel}' cannot be null");"""
    }

    if (!validationConfig.hasValidation) {
      return output.joinToString(
        separator = validationSeparator) {
        "${config.lineIndentation}$it"
      }
    }

    //TODO: Smart trim (only) Strings with StringUtils.abbreviate(x, 128) here
    if (validationConfig.maxSize != null) {
      val operator = if (baseType.isCollection) "size()" else "length()"
      output += """
        |Preconditions.checkArgument(${field.name.lowerCamel}.${operator} <= ${validationConfig.maxSize},
        |  "'${field.name.lowerCamel}' is too large: maxSize=${validationConfig.maxSize}");         
        """.trimMargin()
    }

    if (validationConfig.minSize != null) {
      val operator = if (baseType.isCollection) "size()" else "length()"

      output += """
        |Preconditions.checkArgument(${field.name.lowerCamel}.${operator} <= ${validationConfig.minSize}
        |  "'${field.name.lowerCamel}' is too small: minSize=${validationConfig.minSize}");         
        """.trimMargin()
    }

    if (validationConfig.requireNotBlank != null && validationConfig.requireNotBlank) {
      output += """
        |Preconditions.checkArgument(
        |  StringUtils.isNotBlank(${field.name.lowerCamel}), "'${field.name.lowerCamel}' is required and blank");
        """.trimMargin()
    }

    if (validationConfig.requireTrimmed != null && validationConfig.requireTrimmed) {
      output += """
        |Preconditions.checkArgument(
        |  Objects.equals(
        |    StringUtils.trim(${field.name.lowerCamel}),
        |    ${field.name.lowerCamel}),  
        |  "'${field.name.lowerCamel}' must be trimmed");
        """.trimMargin()
    }

    if (validationConfig.requireLowerCase != null && validationConfig.requireLowerCase) {
      output += """
        |Preconditions.checkArgument(
        |  ${field.name.lowerCamel}.toLowerCase() == ${field.name.lowerCamel},
        |  "'${field.name.lowerCamel}' must be lower case: value=$${field.name.lowerCamel});"
        """.trimMargin()
    }

    if (validationConfig.requireUpperCase != null && validationConfig.requireUpperCase) {
      output += """
        |Preconditions.checkArgument(
        |  ${field.name.lowerCamel}.toUpperCase() == ${field.name.lowerCamel},
        |  "'${field.name.lowerCamel}' must be upper case: value=$${field.name.lowerCamel});"
        """.trimMargin()
    }

    if (validationConfig.maxValue != null) {
      output += """
        |Preconditions.checkArgument(
        |  ${field.name.lowerCamel} <= ${validationConfig.maxValue},
        |  "'${field.name.lowerCamel}' is too large: maxValue=${validationConfig.maxValue}, value=" + ${field.name.lowerCamel});       
        """.trimMargin()
    }

    if (validationConfig.minValue != null) {
      output += """
        |Preconditions.checkArgument(
        |  ${field.name.lowerCamel} >= ${validationConfig.minValue},
        |  "'${field.name.lowerCamel}' is too small: minValue=${validationConfig.minValue}, value=" + ${field.name.lowerCamel});        
        """.trimMargin()
    }

    if (validationConfig.after != null) {
      output += """
        |Preconditions.checkArgument(
        |  ${field.name.lowerCamel}.isAfter(${validationConfig.after}),
        |  "'${field.name.lowerCamel}' must be after ${validationConfig.after}, value=$${field.name.lowerCamel});"
        """.trimMargin()
    }

    if (validationConfig.before != null) {
      output += """
        |Preconditions.checkArgument(
        |  ${field.name.lowerCamel}.isBefore(${validationConfig.before}),
        |  "'${field.name.lowerCamel}' must be before ${validationConfig.before}, value=$${field.name.lowerCamel});"
        """.trimMargin()
    }

    if (baseType == PATH && validationConfig.fileConstraint != null) {
      TODO("add validation for file: validationConfig.fileConstraint=${validationConfig.fileConstraint}")
    }

    if (validationConfig.requireMatchesRegex != null) {
      TODO("add validation for validationConfig.requireMatchesRegex=${validationConfig.requireMatchesRegex}")
    }

    check(output.none { it.isBlank() })
    return output.joinToString(
      separator = validationSeparator) {
      "${config.lineIndentation}$it"
    }
  }

  private fun handleKotlin(config: RenderConfig): String {

    if (!validationConfig.hasValidation) {
      return ""
    }

    val baseType = field.effectiveBaseType(KOTLIN_JVM_1_4)
    val output = mutableListOf<String>()

    //TODO: Smart trim with ellipse here
    if (validationConfig.maxSize != null) {
      output += """
        |require(${field.name.lowerCamel}.length <= ${validationConfig.maxSize}){
        |  "'${field.name.lowerCamel}' is too long: maxSize=${validationConfig.maxSize}, value=$${field.name.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.minSize != null) {
      output += """
        |require(${field.name.lowerCamel}.length <= ${validationConfig.minSize}) {
        |  "'${field.name.lowerCamel}' is too short: maxSize=${validationConfig.minSize}, value=$${field.name.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireNotBlank != null && validationConfig.requireNotBlank) {
      output += """
        |require(${field.name.lowerCamel}.isNotBlank()) {
        |  "'${field.name.lowerCamel}' is required and blank"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireTrimmed != null && validationConfig.requireTrimmed) {
      output += """
        |require(${field.name.lowerCamel}.trim() == ${field.name.lowerCamel}) {
        |  "'${field.name.lowerCamel}' must be trimmed"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireLowerCase != null && validationConfig.requireLowerCase) {
      output += """
        |require(${field.name.lowerCamel}.lowercase() == ${field.name.lowerCamel}) {
        |  "'${field.name.lowerCamel}' must be lower case: value=$${field.name.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireUpperCase != null && validationConfig.requireUpperCase) {
      output += """
        |require(${field.name.lowerCamel}.uppercase() == ${field.name.lowerCamel}) {
        |  "'${field.name.lowerCamel}' must be upper case: value=$${field.name.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.maxValue != null) {
      output += """
        |require(${field.name.lowerCamel} <= ${validationConfig.maxValue}) {
        |  "'${field.name.lowerCamel}' is too large: maxValue=${validationConfig.maxValue}, value=$${field.name.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.minValue != null) {
      output += """
        |require(${field.name.lowerCamel} >= ${validationConfig.minValue}) {
        |  "'${field.name.lowerCamel}' is too small: minValue=${validationConfig.minValue}, value=$${field.name.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.after != null) {
      output += """
        |require(${field.name.lowerCamel}.isAfter(${validationConfig.after})) {
        |  "'${field.name.lowerCamel}' must be after ${validationConfig.after}, value=$${field.name.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.before != null) {
      output += """
        |require(${field.name.lowerCamel}.isBefore(${validationConfig.before})) {
        |  "'${field.name.lowerCamel}' must be before ${validationConfig.before}, value=$${field.name.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (baseType == PATH && validationConfig.fileConstraint != null) {
      TODO("add validation for file: validationConfig.fileConstraint=${validationConfig.fileConstraint}")
    }

    if (validationConfig.requireMatchesRegex != null) {
      TODO("add validation for validationConfig.requireMatchesRegex=${validationConfig.requireMatchesRegex}")
    }

    check(output.none { it.isBlank() })
    return output.joinToString(
      separator = validationSeparator) {
      "${config.lineIndentation}$it"
    }
  }
}
