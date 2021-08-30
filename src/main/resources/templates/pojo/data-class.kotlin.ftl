package ${request.packageName.value}

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
<#list entity.java8View.importsForFields as importable>
import ${importable}
</#list>
import java.util.ArrayList
import java.util.Arrays
import java.util.HashSet
import java.util.StringJoiner
import java.util.TreeSet

/**
 * Immutable POJO
 * See ${request.prettyTemplateName}
 */
<#--TODO: include class documentation when present-->
@JsonPropertyOrder(alphabetic = true)
data class ${entity.name.upperCamel}(
  <#list entity.idFields as field>
      /** PK field */
      val ${field.name.lowerCamel}: ${field.kotlinView.typeLiteral},

  </#list>
  // -- Other Fields
  <#list entity.nonIdFields as field>
    <#if field.hasDefault>
        val ${field.name.lowerCamel}: ${field.kotlinView.typeLiteral} = ${field.jvmView.defaultValueLiteral},
    <#else>
        val ${field.name.lowerCamel}: ${field.kotlinView.typeLiteral},
    </#if>
  </#list>
) {

  init {
    <#list entity.validatedFields as field>
        <#if field.validationConfig.maxSize??>

<#--            TODO: allow collections to use .size-->
        require(${field.name.lowerCamel}.length <= ${field.validationConfig.maxSize}) {
<#--            TODO: Smart trim with ellipse here-->
            "'${field.name.lowerCamel}' is too long: maxSize=${field.validationConfig.maxSize}, value=$${field.name.lowerCamel}"
        }
        </#if>
<#-- -->
        <#if field.validationConfig.minSize??>

<#--            TODO: allow collections to use .size-->
        require(${field.name.lowerCamel}.length <= ${field.validationConfig.minSize}) {
<#--            TODO: Smart trim with ellipse here-->
            "'${field.name.lowerCamel}' is too short: minSize=${field.validationConfig.minSize}, value=$${field.name.lowerCamel}"
        }
        </#if>
<#-- -->
        <#if field.validationConfig.requireNotBlank>

        require(${field.name.lowerCamel}.isNotBlank()) {
            "'${field.name.lowerCamel}' is required and blank"
        }
        </#if>
<#-- -->
        <#if field.validationConfig.requireTrimmed>

        require(${field.name.lowerCamel}.trim() == ${field.name.lowerCamel}) {
            "'${field.name.lowerCamel}' must be trimmed: value=$${field.name.lowerCamel}"
        }
        </#if>
<#-- -->
        <#if field.validationConfig.maxValue??>

            //TODO: add maxValue validation for $field
        </#if>
<#-- -->
        <#if field.validationConfig.minValue??>

            //TODO: add minValue validation for $field
        </#if>
<#-- -->
        <#if field.validationConfig.fileConstraint??>

            //TODO: add fileConstraint validation for $field
        </#if>
<#-- -->
        <#if field.validationConfig.after??>

            //TODO: add after validation for $field
        </#if>
<#-- -->
        <#if field.validationConfig.before??>

            //TODO: add before validation for $field
        </#if>
<#-- -->
        <#if field.validationConfig.requireLowerCase>

            //TODO: add requireLowerCase validation for $field
        </#if>
<#-- -->
        <#if field.validationConfig.requireUpperCase>

            //TODO: add requireUpperCase validation for $field
        </#if>
<#-- -->
        <#if field.validationConfig.requireMatchesRegex??>

            //TODO: add requireMatchesRegex validation for $field
        </#if>
    </#list>
  }
}
