package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
  entity.kotlinView.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}

${entity.kotlinView.documentation("Immutable POJO")}
<#--TODO: include class documentation when present-->
@JsonPropertyOrder(alphabetic = true)
data class ${entity.name.upperCamel}(
  ${entity.kotlinView.fieldDeclarations}
) {

  init {
${entity.kotlinView.validationExpressions}
${entity.kotlinView.interFieldValidationExpressions}
  }
}
