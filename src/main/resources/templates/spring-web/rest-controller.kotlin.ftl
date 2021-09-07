package ${request.packageName.value}
${request.jvmView.templateDebugInfo}

${request.kotlinView.serializeImports(
entity.kotlinView.importsForFields,
request.extraJVMImports,
request.jvmContextClass)}

// improve

