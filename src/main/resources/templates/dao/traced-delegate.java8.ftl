package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
  entity.java8View.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}

/**
 * OpenTracing based Traced DAO
 * <p>
 * Uses Delegation pattern
 * <p>
 * Relies on the Context class (${request.jvmView.unqualifiedContextClass}) to:
 * 1. provide the current {@link Span}
 * 2. build a new child Context, with a new {@link Span}
  * <p>
 * Threadsafe & Reusable after construction
 */
public final class ${entity.name.upperCamel}TracedDAO implements ${entity.name.upperCamel}DAO {

  private static final String ENTITY_TYPE_NAME = "${entity.name.upperCamel}";

  /**
   * Delegation Pattern: All DAO calls are delegated to this
   */
  private final ${entity.name.upperCamel}DAO realDAO;

  /**
   * Converts exception to a string for {@link io.opentracing.Span#setTag}
   */
  private final Function<${r"Exception"}, String> exceptionSerializer;

  private final Tracer tracer;

  public ${entity.name.upperCamel}TracedDAO(
      ${entity.name.upperCamel}DAO realDAO,
      Tracer tracer,
      Function<${r"Exception"}, String> exceptionSerializer) {

    Objects.requireNonNull(exceptionSerializer, "exceptionSerializer is required");
    Objects.requireNonNull(realDAO, "realDAO is required");
    Objects.requireNonNull(tracer, "tracer is required");

    this.exceptionSerializer = exceptionSerializer;
    this.realDAO = realDAO;
    this.tracer = tracer;
  }

<#if entity.hasIdFields>
  @Override
  public void delete(${request.jvmView.unqualifiedContextClass} context, ${entity.java8View.methodArgsForIdFields(false)}) {
    Objects.requireNonNull(context, "context is required and missing.");
    //TODO: preconditions on ok field(s)

    final Span span = tracer.buildSpan("jdbc::delete")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        <#list entity.idFields as pk>
        .withTag("${pk.name.lowerCamel}", String.valueOf(${pk.name.lowerCamel}))
        </#list>
        .start();

    final ${request.jvmView.unqualifiedContextClass} childContext = context.withCurrentSpan(span);
    wrapDAOCall(
        span,
        () -> realDAO.delete(childContext, ${entity.jvmView.commaSeparatedIdFieldNames}));
  }

  @Override
  public boolean exists(${request.jvmView.unqualifiedContextClass} context, ${entity.java8View.methodArgsForIdFields(false)}) {
    Objects.requireNonNull(context, "context is required and missing.");
    //TODO: preconditions on ok field(s)

    final Span span = tracer.buildSpan("jdbc::exists")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        <#list entity.idFields as pk>
        .withTag("${pk.name.lowerCamel}", String.valueOf(${pk.name.lowerCamel}))
        </#list>
        .start();

      final ${request.jvmView.unqualifiedContextClass} childContext = context.withCurrentSpan(span);
      return wrapDAOCall(
        span,
        () -> realDAO.exists(childContext, ${entity.jvmView.commaSeparatedIdFieldNames}));
  }

  @Override
  public ${entity.name.upperCamel} findById(${request.jvmView.unqualifiedContextClass} context, ${entity.java8View.methodArgsForIdFields(false)}) {
    Objects.requireNonNull(context, "context is required and missing.");
    //TODO: preconditions on ok field(s)

    final Span span = tracer.buildSpan("jdbc::findById")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        <#list entity.idFields as pk>
        .withTag("${pk.name.lowerCamel}", String.valueOf(${pk.name.lowerCamel}))
        </#list>
        .start();

      final ${request.jvmView.unqualifiedContextClass} childContext = context.withCurrentSpan(span);
      return wrapDAOCall(
        span,
        () -> realDAO.findById(childContext, ${entity.jvmView.commaSeparatedIdFieldNames}));
  }
</#if>
  @Override
  public void create(${request.jvmView.unqualifiedContextClass} context, ${entity.name.upperCamel} entity) {
    Objects.requireNonNull(context, "context is required and missing.");
    Objects.requireNonNull(entity, "entity is required and missing.");

    final Span span = tracer.buildSpan("jdbc::create")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        <#list entity.idFields as pk>
        .withTag("${pk.name.lowerCamel}", String.valueOf(entity.get${pk.name.upperCamel}()))
        </#list>
        .start();

    final ${request.jvmView.unqualifiedContextClass} childContext = context.withCurrentSpan(span);
    wrapDAOCall(
        span,
        () -> realDAO.create(childContext, entity));
  }

  @Override
  public List<${entity.name.upperCamel}> list(${request.jvmView.unqualifiedContextClass} context) {
    Objects.requireNonNull(context, "context is required and missing.");

    final Span span = tracer.buildSpan("jdbc::list")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start();

    final ${request.jvmView.unqualifiedContextClass} childContext = context.withCurrentSpan(span);
    return wrapDAOCall(
        span,
        () -> realDAO.list(childContext));
  }

<#if entity.hasNonIdFields>
  @Override
  public void update(${request.jvmView.unqualifiedContextClass} context, ${entity.name.upperCamel} entity) {
    Objects.requireNonNull(context, "context is required and missing.");
    Objects.requireNonNull(entity, "entity is required and missing.");

    final Span span = tracer.buildSpan("jdbc::update")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start();

    final ${request.jvmView.unqualifiedContextClass} childContext = context.withCurrentSpan(span);
    wrapDAOCall(
        span,
        () -> realDAO.update(childContext, entity));
  }

</#if>
  @Override
  public void upsert(${request.jvmView.unqualifiedContextClass} context, ${entity.name.upperCamel} entity) {
    Objects.requireNonNull(context, "context is required and missing.");
    Objects.requireNonNull(entity, "entity is required and missing.");

    final Span span = tracer.buildSpan("jdbc::upsert")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .start();

    final ${request.jvmView.unqualifiedContextClass} childContext = context.withCurrentSpan(span);
    wrapDAOCall(
        span,
        () -> realDAO.upsert(childContext, entity));
  }

<#list entity.patchableFields as field>
  // -- Patch methods
  @Override
  public void set${field.name.upperCamel}(
      ${request.jvmView.unqualifiedContextClass} context,
      ${entity.java8View.methodArgsForIdFields(false)},
      ${field.java8View.unqualifiedType} ${field.name.lowerCamel}) {

    Objects.requireNonNull(context, "context is required and null.");
    <#if field.type.nullable>
    //TODO: requireNonNull precondition on ${field.java8View.unqualifiedType} (except for primitives)
    </#if>

    //TODO: field validation here (since not part of the POJO validation)

    final Span span = tracer.buildSpan("jdbc::patch")
        .asChildOf(context.currentSpan())
        .ignoreActiveSpan()
        .withTag("entityType", ENTITY_TYPE_NAME)
        .withTag("fieldName", "${field.name.lowerCamel}")
        .start();

    wrapDAOCall(
        span,
        () -> realDAO.set${field.name.upperCamel}(
            context.withCurrentSpan(span),
            ${entity.jvmView.commaSeparatedIdFieldNames},
            ${field.name.lowerCamel}));
  }

</#list>

  /**
   * Wrap the DAO call with proper span cleanup
   */
  private void wrapDAOCall(Span span, Runnable daoCall) {
    Objects.requireNonNull(span, "span is required and missing.");
    Objects.requireNonNull(daoCall, "daoCall is required and missing.");

    try {
      daoCall.run();

    } catch (Exception ex) {
      applyExceptionToSpan(span, ex);
      throw ex;

    } finally {
      span.finish();
    }
  }

  /**
   * Wrap the DAO call with proper span cleanup
   */
  private <T> T wrapDAOCall(Span span, Supplier<T> daoCall) {
    Objects.requireNonNull(span, "span is required and missing.");
    Objects.requireNonNull(daoCall, "daoCall is required and missing.");

    try {
      return daoCall.get();

    } catch (Exception ex) {
      applyExceptionToSpan(span, ex);
      throw ex;

    } finally {
      span.finish();
    }
  }

  private void applyExceptionToSpan(Span span, Exception ex) {
    span.setTag("error", true);
    span.setTag("error.kind", ex.getClass().getName());
    span.setTag("error.object", exceptionSerializer.apply(ex));
  }
}
