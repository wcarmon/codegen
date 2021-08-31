package ${request.packageName.value}
${request.jvmView.templateNameComment}

${request.kotlinView.serializeImports(
  entity.kotlinView.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}

/**
 * Immutable POJO
 */
<#--TODO: include class documentation when present-->
@JsonPropertyOrder(alphabetic = true)
data class ${entity.name.upperCamel}(
  <#-- TODO: use expressions for these declarations -->
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
        ${field.kotlinView.validationExpressions}
    </#list>
  }
}
