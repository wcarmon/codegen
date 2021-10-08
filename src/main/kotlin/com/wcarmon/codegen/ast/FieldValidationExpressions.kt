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

  val tableConstraintPrefix: String = "",
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

      //TODO: add other databases which are compatible with postgresql
      SQL_POSTGRESQL,
      -> handlePostgreSQL(config)

      else -> TODO("handle Field validation for $config")
    }.trimEnd()

  //TODO: fix indentation on multi-line validations
  private fun handleJava(config: RenderConfig): String {
    if (!validationConfig.hasValidation) {
      return ""
    }

    val fName = field.name.lowerCamel
    val baseType = field.effectiveBaseType(JAVA_08)
    val output = mutableListOf<String>()

    // GOTCHA: Java type system doesn't enforce null safety, so we do it manually
    if (!isPrimitive(field) && !field.type.nullable) {
      output +=
        """Objects.requireNonNull($fName, "'$fName' cannot be null");"""
    }

    if (!validationConfig.hasValidation) {
      return output.joinToString(
        separator = validationSeparator
      ) {
        "${config.lineIndentation}$it"
      }
    }

    //TODO: Smart trim (only) Strings with StringUtils.abbreviate(x, 128) here
    if (validationConfig.maxSize != null) {
      val operator = if (baseType.isCollection) "size()" else "length()"
      output += """
        |Preconditions.checkArgument($fName.${operator} <= ${validationConfig.maxSize},
        |  "'$fName' is too large: maxSize=${validationConfig.maxSize}");         
        """.trimMargin()
    }

    if (validationConfig.minSize != null) {
      val operator = if (baseType.isCollection) "size()" else "length()"

      output += """
        |Preconditions.checkArgument($fName.${operator} <= ${validationConfig.minSize}
        |  "'$fName' is too small: minSize=${validationConfig.minSize}");         
        """.trimMargin()
    }

    if (validationConfig.requireNotBlank != null && validationConfig.requireNotBlank) {
      output += """
        |Preconditions.checkArgument(
        |  StringUtils.isNotBlank($fName), "'$fName' is required and blank");
        """.trimMargin()
    }

    if (validationConfig.requireTrimmed != null && validationConfig.requireTrimmed) {
      output += """
        |Preconditions.checkArgument(
        |  Objects.equals(
        |    StringUtils.trim($fName),
        |    $fName),  
        |  "'$fName' must be trimmed");
        """.trimMargin()
    }

    if (validationConfig.requireLowerCase != null && validationConfig.requireLowerCase) {
      output += """
        |Preconditions.checkArgument(
        |  $fName.toLowerCase() == $fName,
        |  "'$fName' must be lower case: value=$$fName);"
        """.trimMargin()
    }

    if (validationConfig.requireUpperCase != null && validationConfig.requireUpperCase) {
      output += """
        |Preconditions.checkArgument(
        |  $fName.toUpperCase() == $fName,
        |  "'$fName' must be upper case: value=$$fName);"
        """.trimMargin()
    }

    if (validationConfig.maxValue != null) {
      output += """
        |Preconditions.checkArgument(
        |  $fName <= ${validationConfig.maxValue},
        |  "'$fName' is too large: maxValue=${validationConfig.maxValue}, value=" + $fName);       
        """.trimMargin()
    }

    if (validationConfig.minValue != null) {
      output += """
        |Preconditions.checkArgument(
        |  $fName >= ${validationConfig.minValue},
        |  "'$fName' is too small: minValue=${validationConfig.minValue}, value=" + $fName);        
        """.trimMargin()
    }

    if (validationConfig.after != null) {
      output += """
        |Preconditions.checkArgument(
        |  $fName.isAfter(${validationConfig.after}),
        |  "'$fName' must be after ${validationConfig.after}, value=$$fName);"
        """.trimMargin()
    }

    if (validationConfig.before != null) {
      output += """
        |Preconditions.checkArgument(
        |  $fName.isBefore(${validationConfig.before}),
        |  "'$fName' must be before ${validationConfig.before}, value=$$fName);"
        """.trimMargin()
    }

    if (baseType == PATH && validationConfig.fileConstraint != null) {
      TODO("add validation for file: validationConfig.fileConstraint=${validationConfig.fileConstraint}")
    }

    if (validationConfig.requireMatchesRegex != null) {
      output += """
        |Preconditions.checkArgument(
        |  Pattern.matches("${validationConfig.requireMatchesRegex}", $fName),        
        |  "'$fName' must match regex: ${validationConfig.requireMatchesRegex}, value=" + $fName);
        """.trimMargin()
    }

    check(output.none { it.isBlank() })
    return output.joinToString(
      separator = validationSeparator
    ) {
      "${config.lineIndentation}$it"
    }
  }

  private fun handleKotlin(config: RenderConfig): String {

    if (!validationConfig.hasValidation) {
      return ""
    }

    val fName = field.name.lowerCamel
    val baseType = field.effectiveBaseType(KOTLIN_JVM_1_4)
    val output = mutableListOf<String>()

    //TODO: Smart trim with ellipse here
    if (validationConfig.maxSize != null) {
      output += """
        |require($fName.length <= ${validationConfig.maxSize}){
        |  "'$fName' is too long: maxSize=${validationConfig.maxSize}, value=$$fName"
        |}
        """.trimMargin()
    }

    if (validationConfig.minSize != null) {
      output += """
        |require($fName.length <= ${validationConfig.minSize}) {
        |  "'$fName' is too short: maxSize=${validationConfig.minSize}, value=$$fName"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireNotBlank != null && validationConfig.requireNotBlank) {
      output += """
        |require($fName.isNotBlank()) {
        |  "'$fName' is required and blank"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireTrimmed != null && validationConfig.requireTrimmed) {
      output += """
        |require($fName.trim() == $fName) {
        |  "'$fName' must be trimmed"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireLowerCase != null && validationConfig.requireLowerCase) {
      output += """
        |require($fName.lowercase() == $fName) {
        |  "'$fName' must be lower case: value=$$fName"
        |}
        """.trimMargin()
    }

    if (validationConfig.requireUpperCase != null && validationConfig.requireUpperCase) {
      output += """
        |require($fName.uppercase() == $fName) {
        |  "'$fName' must be upper case: value=$$fName"
        |}
        """.trimMargin()
    }

    if (validationConfig.maxValue != null) {
      output += """
        |require($fName <= ${validationConfig.maxValue}) {
        |  "'$fName' is too large: maxValue=${validationConfig.maxValue}, value=$$fName"
        |}
        """.trimMargin()
    }

    if (validationConfig.minValue != null) {
      output += """
        |require($fName >= ${validationConfig.minValue}) {
        |  "'$fName' is too small: minValue=${validationConfig.minValue}, value=$$fName"
        |}
        """.trimMargin()
    }

    if (validationConfig.after != null) {
      output += """
        |require($fName.isAfter(${validationConfig.after})) {
        |  "'$fName' must be after ${validationConfig.after}, value=$$fName"
        |}
        """.trimMargin()
    }

    if (validationConfig.before != null) {
      output += """
        |require($fName.isBefore(${validationConfig.before})) {
        |  "'$fName' must be before ${validationConfig.before}, value=$$fName"
        |}
        """.trimMargin()
    }

    if (baseType == PATH && validationConfig.fileConstraint != null) {
      TODO("add validation for file: validationConfig.fileConstraint=${validationConfig.fileConstraint}")
    }

    if (validationConfig.requireMatchesRegex != null) {
      output += """
        |require(Regex("${validationConfig.requireMatchesRegex}") matches $fName) {
        |  "'$fName' must match regex ${validationConfig.requireMatchesRegex}, value=$$fName"
        |}
        """.trimMargin()
    }

    check(output.none { it.isBlank() })
    return output.joinToString(
      separator = validationSeparator
    ) {
      "${config.lineIndentation}$it"
    }
  }

  private fun handlePostgreSQL(config: RenderConfig): String {
    if (!validationConfig.hasValidation) {
      return ""
    }

    val fName = field.name.lowerSnake

    val nullSafeSnippet = if (field.type.nullable) {
      "\"$fName\" IS NULL OR "
    } else {
      ""
    }

    val output = mutableListOf<String>()

    if (validationConfig.minValue != null) {
      output += """CONSTRAINT ${tableConstraintPrefix}${fName}_min_value CHECK (${nullSafeSnippet}"$fName" >= ${validationConfig.minValue})"""
    }

    if (validationConfig.maxValue != null) {
      output += """CONSTRAINT ${tableConstraintPrefix}${fName}_max_value CHECK (${nullSafeSnippet}"$fName" <= ${validationConfig.maxValue})"""
    }

    if (validationConfig.requireNotBlank != null && validationConfig.requireNotBlank) {
      output += """CONSTRAINT ${tableConstraintPrefix}${fName}_not_blank CHECK (${nullSafeSnippet}LENGTH(TRIM("$fName")) > 0)"""
    }

    if (validationConfig.requireTrimmed != null && validationConfig.requireTrimmed) {
      output += """CONSTRAINT ${tableConstraintPrefix}${fName}_trimmed CHECK (${nullSafeSnippet}TRIM("$fName") = "$fName")"""
    }

    if (validationConfig.requireLowerCase != null && validationConfig.requireLowerCase) {
      output += """CONSTRAINT ${tableConstraintPrefix}${fName}_lower_case CHECK (${nullSafeSnippet}LOWER("$fName") = "$fName")"""
    }

    if (validationConfig.requireUpperCase != null && validationConfig.requireUpperCase) {
      output += """CONSTRAINT ${tableConstraintPrefix}${fName}_upper_case CHECK (${nullSafeSnippet}UPPER("$fName") = "$fName")"""
    }

    //TODO: after <-- requires same date-time serialization mechanism
    //TODO: before <-- requires same date-time serialization mechanism
    //  JVM:        2021-10-01T16:50:11.628135Z
    //  POSTGRESQL: 2021-10-01 16:50:11.628135 +00:00
    //            TO_CHAR((NOW() AT TIME ZONE 'UTC'), 'YYYY-MM-DD"T"HH24:MI:SS.US"Z"')

    //TODO: fileConstraint <-- impossible (different file system)
    //TODO: maxSize - https://www.postgresql.org/docs/current/arrays.html
    //TODO: minSize - https://www.postgresql.org/docs/current/arrays.html
    //TODO: requireMatchesRegex - https://www.postgresql.org/docs/current/functions-matching.html

    check(output.none { it.isBlank() })
    return output.joinToString(
      separator = validationSeparator
    ) {
      "${config.lineIndentation}$it"
    }
  }
}
