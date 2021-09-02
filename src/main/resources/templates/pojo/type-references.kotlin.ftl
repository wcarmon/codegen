@file:JvmName("TypeReferences")
package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
request.extraJVMImports,
request.jvmContextClass)}

<#list entities as entity>
${entity.kotlinView.typeReferenceDeclarations}

</#list>
