package ${request.packageName.value}
${request.golangView.templateDebugInfo}

${request.golangView.serializeImports(request.extraGolangImports)}

<#list entities as entity>
// Database interaction for ${entity.name.upperCamel}
type ${entity.name.upperCamel}DAO interface {

  // Delete at-most-one existing ${entity.name.upperCamel} instance
  // NOOP if no matching entity exists
  Delete${entity.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) error

  ${entity.name.upperCamel}Exists(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (bool, error)

  // return nil if not found
  FindById${entity.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (*${entity.name.upperCamel}, error)

  Create${entity.name.upperCamel}(ctx context.Context, entity ${entity.name.upperCamel}) error

  Update${entity.name.upperCamel}(ctx context.Context, entity ${entity.name.upperCamel}) error

  List${entity.name.upperCamel}(ctx context.Context) ([]${entity.name.upperCamel}, error)

<#list entity.nonIdFields as field>
  Set${field.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}, ${field.name.lowerCamel} ${field.golangView.typeLiteral}) error

</#list>
}

</#list>
