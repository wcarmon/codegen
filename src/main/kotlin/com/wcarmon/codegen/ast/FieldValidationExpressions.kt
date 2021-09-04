package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.BaseFieldType.PATH
import com.wcarmon.codegen.model.FieldValidation
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.util.isPrimitive

/**
 * Builds 1 or more expressions for the field.
 * Validations are performed in a safe order.
 */
data class FieldValidationExpressions(
  val fieldName: Name,
  val type: LogicalFieldType,
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

    val output = mutableListOf<String>()

    // GOTCHA: Java type system doesn't enforce null safety, so we do it manually
    if (!isPrimitive(type) && !type.nullable) {
      output +=
        """Objects.requireNonNull(${fieldName.lowerCamel}, "'${fieldName.lowerCamel}' cannot be null");"""
    }

    if (!validationConfig.hasValidation) {
      return output.joinToString(
        separator = validationSeparator) {
        "${config.lineIndentation}$it"
      }
    }

    //TODO: Smart trim (only) Strings with StringUtils.abbreviate(x, 128) here
    if (validationConfig.maxSize != null) {
      val operator = if (type.base.isCollection) "size()" else "length()"
      output += """
        |Preconditions.checkArgument(${fieldName.lowerCamel}.${operator} <= ${validationConfig.maxSize},
        |  "'${fieldName.lowerCamel}' is too large: maxSize=${validationConfig.maxSize}");         
        """.trimMargin()
    }

    if (validationConfig.minSize != null) {
      val operator = if (type.base.isCollection) "size()" else "length()"

      output += """
        |Preconditions.checkArgument(${fieldName.lowerCamel}.${operator} <= ${validationConfig.minSize}
        |  "'${fieldName.lowerCamel}' is too small: minSize=${validationConfig.minSize}");         
        """.trimMargin()
    }

    if (validationConfig.requireNotBlank != null && validationConfig.requireNotBlank) {
      output += """
        |Preconditions.checkArgument(
        |  StringUtils.isNotBlank(${fieldName.lowerCamel}), "'${fieldName.lowerCamel}' is required and blank");
        """.trimMargin()
    }

    if (validationConfig.requireTrimmed != null && validationConfig.requireTrimmed) {
      output += """
        |Preconditions.checkArgument(
        |  Objects.equals(
        |    StringUtils.trim(${fieldName.lowerCamel}),
        |    ${fieldName.lowerCamel}),  
        |  "'${fieldName.lowerCamel}' must be trimmed");
        """.trimMargin()
    }

    if (validationConfig.requireLowerCase != null && validationConfig.requireLowerCase) {
      output += """
        |Preconditions.checkArgument(
        |  ${fieldName.lowerCamel}.toLowerCase() == ${fieldName.lowerCamel},
        |  "'${fieldName.lowerCamel}' must be lower case: value=$${fieldName.lowerCamel});"
        """.trimMargin()
    }

    if (validationConfig.requireUpperCase != null && validationConfig.requireUpperCase) {
      output += """
        |Preconditions.checkArgument(
        |  ${fieldName.lowerCamel}.toUpperCase() == ${fieldName.lowerCamel},
        |  "'${fieldName.lowerCamel}' must be upper case: value=$${fieldName.lowerCamel});"
        """.trimMargin()
    }

    if (validationConfig.maxValue != null) {
      output += """
        |Preconditions.checkArgument(
        |  ${fieldName.lowerCamel} <= ${validationConfig.maxValue},
        |  "'${fieldName.lowerCamel}' is too large: maxValue=${validationConfig.maxValue}, value=" + ${fieldName.lowerCamel});       
        """.trimMargin()
    }

    if (validationConfig.minValue != null) {
      output += """
        |Preconditions.checkArgument(
        |  ${fieldName.lowerCamel} >= ${validationConfig.minValue},
        |  "'${fieldName.lowerCamel}' is too small: minValue=${validationConfig.minValue}, value=" + ${fieldName.lowerCamel});        
        """.trimMargin()
    }

    if (validationConfig.after != null) {
      output += """
        |Preconditions.checkArgument(
        |  ${fieldName.lowerCamel}.isAfter(${validationConfig.after}),
        |  "'${fieldName.lowerCamel}' must be after ${validationConfig.after}, value=$${fieldName.lowerCamel});"
        """.trimMargin()
    }

    if (validationConfig.before != null) {
      output += """
        |Preconditions.checkArgument(
        |  ${fieldName.lowerCamel}.isBefore(${validationConfig.before}),
        |  "'${fieldName.lowerCamel}' must be before ${validationConfig.before}, value=$${fieldName.lowerCamel});"
        """.trimMargin()
    }

    if (type.base == PATH && validationConfig.fileConstraint != null) {
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

    val output = mutableListOf<String>()

    //TODO: Smart trim with ellipse here
    if (validationConfig.maxSize != null) {
      output += """
        |require(${fieldName.lowerCamel}.length <= ${validationConfig.maxSize}){
        |  "'${fieldName.lowerCamel}' is too long: maxSize=${validationConfig.maxSize}, value=$${fieldName.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.minSize != null) {
      output += """
        |require(${fieldName.lowerCamel}.length <= ${validationConfig.minSize}) {
        |  "'${fieldName.lowerCamel}' is too short: maxSize=${validationConfig.minSize}, value=$${fieldName.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireNotBlank != null && validationConfig.requireNotBlank) {
      output += """
        |require(${fieldName.lowerCamel}.isNotBlank()) {
        |  "'${fieldName.lowerCamel}' is required and blank"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireTrimmed != null && validationConfig.requireTrimmed) {
      output += """
        |require(${fieldName.lowerCamel}.trim() == ${fieldName.lowerCamel}) {
        |  "'${fieldName.lowerCamel}' must be trimmed"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireLowerCase != null && validationConfig.requireLowerCase) {
      output += """
        |require(${fieldName.lowerCamel}.lowercase() == ${fieldName.lowerCamel}) {
        |  "'${fieldName.lowerCamel}' must be lower case: value=$${fieldName.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireUpperCase != null && validationConfig.requireUpperCase) {
      output += """
        |require(${fieldName.lowerCamel}.uppercase() == ${fieldName.lowerCamel}) {
        |  "'${fieldName.lowerCamel}' must be upper case: value=$${fieldName.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.maxValue != null) {
      output += """
        |require(${fieldName.lowerCamel} <= ${validationConfig.maxValue}) {
        |  "'${fieldName.lowerCamel}' is too large: maxValue=${validationConfig.maxValue}, value=$${fieldName.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.minValue != null) {
      output += """
        |require(${fieldName.lowerCamel} >= ${validationConfig.minValue}) {
        |  "'${fieldName.lowerCamel}' is too small: minValue=${validationConfig.minValue}, value=$${fieldName.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.after != null) {
      output += """
        |require(${fieldName.lowerCamel}.isAfter(${validationConfig.after})) {
        |  "'${fieldName.lowerCamel}' must be after ${validationConfig.after}, value=$${fieldName.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (validationConfig.before != null) {
      output += """
        |require(${fieldName.lowerCamel}.isBefore(${validationConfig.before})) {
        |  "'${fieldName.lowerCamel}' must be before ${validationConfig.before}, value=$${fieldName.lowerCamel}"
        |}
        """.trimMargin()
    }

    if (type.base == PATH && validationConfig.fileConstraint != null) {
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
