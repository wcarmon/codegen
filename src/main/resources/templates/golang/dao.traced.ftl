package ${request.packageName.value}
${request.golangView.templateDebugInfo}

${request.golangView.serializeImports(request.extraGolangImports)}

<#list entities as entity>
type ${entity.name.upperCamel}TracedDAO struct {
  realDao ${entity.name.upperCamel}DAO
  tracer  opentracing.Tracer
}

func (dao *${entity.name.upperCamel}TracedDAO) Delete${entity.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (deleted bool, err error) {

    var options []opentracing.StartSpanOption
    options = append(options, opentracing.Tag{Key: "entityType", Value: "${entity.name.upperCamel}"})
<#list entity.idFields as pk>
    options = append(options, opentracing.Tag{Key: "${pk.name.lowerCamel}", Value: ${pk.name.lowerCamel}})
</#list>

    parentSpan := opentracing.SpanFromContext(ctx)
    if parentSpan != nil {
        options = append(options, opentracing.ChildOf(parentSpan.Context()))
    }

    span := dao.tracer.StartSpan(
      "sql::delete",
      options...,
    )
    defer span.Finish()

    childCtx := opentracing.ContextWithSpan(ctx, span)
    return dao.realDao.Delete${entity.name.upperCamel}(childCtx, ${entity.golangView.commaSeparatedIdFieldNames})
}

func (dao *${entity.name.upperCamel}TracedDAO) ${entity.name.upperCamel}Exists(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (bool, error) {
    var options []opentracing.StartSpanOption
    options = append(options, opentracing.Tag{Key: "entityType", Value: "${entity.name.upperCamel}"})
<#list entity.idFields as pk>
    options = append(options, opentracing.Tag{Key: "${pk.name.lowerCamel}", Value: ${pk.name.lowerCamel}})
</#list>

    parentSpan := opentracing.SpanFromContext(ctx)
    if parentSpan != nil {
        options = append(options, opentracing.ChildOf(parentSpan.Context()))
    }

    span := dao.tracer.StartSpan(
      "sql::exists",
      options...,
    )
    defer span.Finish()

    childCtx := opentracing.ContextWithSpan(ctx, span)
    return dao.realDao.${entity.name.upperCamel}Exists(childCtx, ${entity.golangView.commaSeparatedIdFieldNames})
}

func (dao *${entity.name.upperCamel}TracedDAO) FindById${entity.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (*${entity.name.upperCamel}, error) {
    var options []opentracing.StartSpanOption
    options = append(options, opentracing.Tag{Key: "entityType", Value: "${entity.name.upperCamel}"})
<#list entity.idFields as pk>
    options = append(options, opentracing.Tag{Key: "${pk.name.lowerCamel}", Value: ${pk.name.lowerCamel}})
</#list>

    parentSpan := opentracing.SpanFromContext(ctx)
    if parentSpan != nil {
        options = append(options, opentracing.ChildOf(parentSpan.Context()))
    }

    span := dao.tracer.StartSpan(
      "sql::findById",
      options...,
    )
    defer span.Finish()

    childCtx := opentracing.ContextWithSpan(ctx, span)
    return dao.realDao.FindById${entity.name.upperCamel}(childCtx, ${entity.golangView.commaSeparatedIdFieldNames})
}

func (dao *${entity.name.upperCamel}TracedDAO) Create${entity.name.upperCamel}(ctx context.Context, entity ${entity.name.upperCamel}) error {
    var options []opentracing.StartSpanOption
    options = append(options, opentracing.Tag{Key: "entityType", Value: "${entity.name.upperCamel}"})
    <#list entity.idFields as pk>
    options = append(options, opentracing.Tag{Key: "${pk.name.lowerCamel}", Value: entity.${pk.name.upperCamel}})
    </#list>

    parentSpan := opentracing.SpanFromContext(ctx)
    if parentSpan != nil {
      options = append(options, opentracing.ChildOf(parentSpan.Context()))
    }

    span := dao.tracer.StartSpan(
      "sql::create",
      options...,
    )
    defer span.Finish()

    childCtx := opentracing.ContextWithSpan(ctx, span)
    return dao.realDao.Create${entity.name.upperCamel}(childCtx, entity)
}

func (dao *${entity.name.upperCamel}TracedDAO) Update${entity.name.upperCamel}(ctx context.Context, entity ${entity.name.upperCamel}) error {
    var options []opentracing.StartSpanOption
    options = append(options, opentracing.Tag{Key: "entityType", Value: "${entity.name.upperCamel}"})
    <#list entity.idFields as pk>
    options = append(options, opentracing.Tag{Key: "${pk.name.lowerCamel}", Value: entity.${pk.name.upperCamel}})
    </#list>

    parentSpan := opentracing.SpanFromContext(ctx)
    if parentSpan != nil {
      options = append(options, opentracing.ChildOf(parentSpan.Context()))
    }

    span := dao.tracer.StartSpan(
      "sql::update",
      options...,
    )
    defer span.Finish()

    childCtx := opentracing.ContextWithSpan(ctx, span)
    return dao.realDao.Update${entity.name.upperCamel}(childCtx, entity)
}

func (dao *${entity.name.upperCamel}TracedDAO) List${entity.name.upperCamel}(ctx context.Context) ([]${entity.name.upperCamel}, error) {
    var options []opentracing.StartSpanOption
    options = append(options, opentracing.Tag{Key: "entityType", Value: "${entity.name.upperCamel}"})

    parentSpan := opentracing.SpanFromContext(ctx)
    if parentSpan != nil {
      options = append(options, opentracing.ChildOf(parentSpan.Context()))
    }

    span := dao.tracer.StartSpan(
      "sql::list",
      options...,
    )
    defer span.Finish()

    childCtx := opentracing.ContextWithSpan(ctx, span)
    return dao.realDao.List${entity.name.upperCamel}(childCtx)
}

<#list entity.nonIdFields as field>
func (dao *${entity.name.upperCamel}TracedDAO) Set${field.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}, ${field.name.lowerCamel} ${field.golangView.typeLiteral}) error {
    var options []opentracing.StartSpanOption
    options = append(options, opentracing.Tag{Key: "entityType", Value: "${entity.name.upperCamel}"})
<#list entity.idFields as pk>
    options = append(options, opentracing.Tag{Key: "${pk.name.lowerCamel}", Value: ${pk.name.lowerCamel}})
</#list>

    parentSpan := opentracing.SpanFromContext(ctx)
    if parentSpan != nil {
        options = append(options, opentracing.ChildOf(parentSpan.Context()))
    }

    span := dao.tracer.StartSpan(
      "sql::patch",
      options...,
    )
    defer span.Finish()

    childCtx := opentracing.ContextWithSpan(ctx, span)
    return dao.realDao.Set${field.name.upperCamel}(childCtx, ${entity.golangView.commaSeparatedIdFieldNames}, ${field.name.lowerCamel})
}

</#list>

</#list>
