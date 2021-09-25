package ${request.packageName.value}
${request.golangView.templateDebugInfo}

${request.golangView.serializeImports(request.extraGolangImports)}

<#list entities as entity>
type ${entity.name.upperCamel} struct {
<#list entity.idFields as field>
    ${field.name.upperCamel} ${field.golangView.typeLiteral}
</#list>
<#list entity.nonIdFields as field>
    ${field.name.upperCamel} ${field.golangView.typeLiteral}
</#list>
}

</#list>
