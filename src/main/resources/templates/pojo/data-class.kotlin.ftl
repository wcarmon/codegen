package ${request.packageName.value}

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder
<#list entity.javaImportsForFields as importable>
import ${importable};
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
##TODO: include class documentation when present
@JsonPropertyOrder(alphabetic = true)
data class ${entity.name.upperCamel}(
  <#list entity.primaryKeyFields as field>
      /** PK field */
      val $field.name.lowerCamel: $field.kotlinType,

  </#list>
  // -- Other Fields
  <#list entity.nonPrimaryKeyFields as field>
    <#if field.hasDefault>
        val $field.name.lowerCamel: $field.kotlinType = $field.defaultValueLiteralForJVM,
    <#else>
        val $field.name.lowerCamel: $field.kotlinType,
    </#if>
  </#list>
) {

  init {
    <#list entity.fieldsWithValidation as field>
        <#if field.validation.maxSize??>

            ##TODO: allow collections to use .size
        require(${field.name.lowerCamel}.length <= $field.validation.maxSize) {
            ##TODO: Smart trim with ellipse here
            "'$field.name.lowerCamel' is too long: maxSize=$field.validation.maxSize, value=$${field.name.lowerCamel}"
        }
        </#if>
##
        <#if field.validation.minSize??>

            ##TODO: allow collections to use .size
        require(${field.name.lowerCamel}.length <= $field.validation.minSize) {
            ##TODO: Smart trim with ellipse here
            "'$field.name.lowerCamel' is too short: minSize=$field.validation.minSize, value=$${field.name.lowerCamel}"
        }
        </#if>
##
        <#if field.validation.requireNotBlank>

        require(${field.name.lowerCamel}.isNotBlank()) {
            "'$field.name.lowerCamel' is required and blank"
        }
        </#if>
##
        <#if field.validation.requireTrimmed>

        require(${field.name.lowerCamel}.trim() == ${field.name.lowerCamel}) {
            "'$field.name.lowerCamel' must be trimmed: value=$${field.name.lowerCamel}"
        }
        </#if>
##
        <#if field.validation.maxValue??>

            //TODO: add maxValue validation for $field
        </#if>
##
        <#if field.validation.minValue??>

            //TODO: add minValue validation for $field
        </#if>
##
        <#if field.validation.fileConstraint??>

            //TODO: add fileConstraint validation for $field
        </#if>
##
        <#if field.validation.after??>

            //TODO: add after validation for $field
        </#if>
##
        <#if field.validation.before??>

            //TODO: add before validation for $field
        </#if>
##
        <#if field.validation.requireLowerCase>

            //TODO: add requireLowerCase validation for $field
        </#if>
##
        <#if field.validation.requireUpperCase>

            //TODO: add requireUpperCase validation for $field
        </#if>
##
        <#if field.validation.requireMatchesRegex??>

            //TODO: add requireMatchesRegex validation for $field
        </#if>
    </#list>
  }
}
