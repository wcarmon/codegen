package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
request.extraJVMImports,
request.jvmContextClass)}

<#list entities as entity>
fun build${entity.name.upperCamel} (
  faker: Faker = Faker()
): ${entity.name.upperCamel} = ${entity.name.upperCamel}(
    ${entity.kotlinView.fakeFieldAssignments()}
  )

</#list>
