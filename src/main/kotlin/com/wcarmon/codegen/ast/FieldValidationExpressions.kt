package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.FieldValidation
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage.*

/**
 * Builds multiple expressions for the field
 */
data class FieldValidationExpressions(
  val fieldName: Name,
  val type: BaseFieldType,
  val validationConfig: FieldValidation,
) : Expression {

  override val expressionName = FieldValidationExpressions::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig): String {
    if (!validationConfig.hasValidation) {
      return ""
    }

    return when (config.targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4 -> handleKotlin(config)

      else -> TODO()
    }
  }

  //TODO: just core java (no guava)
  private fun handleJava(
    config: RenderConfig,
  ): String {
//    TODO("Not yet implemented")

    return "TODO: fix me"
    // checkArgument(keywords.length() <= 128, "'keywords' is too long: maxSize=128, value=" + keywords);
    // Objects.requireNotNull(keywords.length() <= 128, "'keywords' is too long: maxSize=128, value=" + keywords);

//TODO: fileConstraint
//TODO: maxSize
//TODO: minSize
//TODO: requireMatchesRegex
//TODO: requireNotBlank
//TODO: requireTrimmed
//TODO: requireLowerCase
//TODO: requireUpperCase
//TODO: maxValue
//TODO: minValue
//TODO: after
//TODO: before
  }

  private fun handleKotlin(config: RenderConfig): String {

    val output = StringBuilder(2048)

    //TODO: Smart trim with ellipse here
    if (validationConfig.maxSize != null) {
      output.append("""
        |require(${fieldName.lowerCamel}.length <= ${validationConfig.maxSize}){
        |  "'${fieldName.lowerCamel}' is too long: maxSize=${validationConfig.maxSize}, value=\$${fieldName.lowerCamel}"
        |}
        |
        """.trimMargin())
    }


//TODO: fileConstraint
//TODO: maxSize
//TODO: minSize
//TODO: requireMatchesRegex
//TODO: requireNotBlank
//TODO: requireTrimmed
//TODO: requireLowerCase
//TODO: requireUpperCase
//TODO: maxValue
//TODO: minValue
//TODO: after
//TODO: before

    /*
        <#if field.validationConfig.minSize??>

<#--            TODO: allow collections to use .size-->
        require(${field.name.lowerCamel}.length <= ${field.validationConfig.minSize}) {
<#--            TODO: Smart trim with ellipse here-->
            "'${field.name.lowerCamel}' is too short: minSize=${field.validationConfig.minSize}, value=$${field.name.lowerCamel}"
        }
        </#if>
        <#if field.validationConfig.requireNotBlank>

        require(${field.name.lowerCamel}.isNotBlank()) {
            "'${field.name.lowerCamel}' is required and blank"
        }
        </#if>
        <#if field.validationConfig.requireTrimmed>

        require(${field.name.lowerCamel}.trim() == ${field.name.lowerCamel}) {
            "'${field.name.lowerCamel}' must be trimmed: value=$${field.name.lowerCamel}"
        }
        </#if>

     */

    return output.toString()
  }
}
