package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
request.extraJVMImports,
request.jvmContextClass)}

//TODO: document me
public final class TypeReferences {

  private TypeReferences() {
  }

<#list entities as entity>
  ${entity.java8View.typeReferenceDeclarations}

</#list>
}
