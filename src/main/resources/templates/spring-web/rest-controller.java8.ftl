package ${request.packageName.value};
${request.jvmView.templateDebugInfo}

${request.java8View.serializeImports(
entity.java8View.importsForFields,
request.extraJVMImports,
request.jvmContextClass)}

/**
 * See https://docs.spring.io/spring-framework/docs/current/reference/html/web.html
 */
@RestController
@RequestMapping("/${entity.name.lowerKebab}")
public final class ${entity.name.upperCamel}RESTController {

  private final ${entity.name.upperCamel}DAO ${entity.name.lowerCamel}DAO;
  private final Tracer tracer;

  public ${entity.name.upperCamel}RESTController(
    ${entity.name.upperCamel}DAO ${entity.name.lowerCamel}DAO,
    Tracer tracer
  ) {
    Objects.requireNonNull(${entity.name.lowerCamel}DAO, "${entity.name.lowerCamel}DAO is required and missing.");
    Objects.requireNonNull(tracer, "tracer is required and missing.");

    this.${entity.name.lowerCamel}DAO = ${entity.name.lowerCamel}DAO;
    this.tracer = tracer;
  }

<#if entity.canDelete>
  @DeleteMapping(value = "/todo-fix", produces = "application/json")
  public void delete(
    HttpServletRequest request,
    ${entity.java8View.methodArgsForIdFields(false)}) {

    ${request.jvmView.unqualifiedContextClass} context = null;

    ${entity.name.lowerCamel}DAO.delete(context, ${entity.jvmView.commaSeparatedIdFieldNames});

    //TODO: build response
  }

</#if>
<#if entity.canCheckForExistence>
  @GetMapping(value = "/todo-fix", produces = "application/json")
  public boolean exists(
    HttpServletRequest request,
    ${entity.java8View.methodArgsForIdFields(false)}) {
    throw new RuntimeException("TODO: implement me");
  }

</#if>
<#if entity.canFindById>
  @GetMapping(value = "/todo-fix", produces = "application/json")
  public ${entity.name.upperCamel} findById(
    HttpServletRequest request,
   // TODO: prefix each with @PathVariable
    ${entity.java8View.methodArgsForIdFields(false)}
) {
    ${request.jvmView.unqualifiedContextClass} context = null; //TODO: fix
    // ChronoContext context  <--- build in interceptor?

    //TODO: build response
    return ${entity.name.lowerCamel}DAO.findById(context, ${entity.jvmView.commaSeparatedIdFieldNames});
  }

</#if>
<#if entity.canCreate>
  @PostMapping(value = "/todo-fix", produces = "application/json")
  public void create(
    HttpServletRequest request,
    ${entity.name.upperCamel} entity) {
    ${request.jvmView.unqualifiedContextClass} context = null;

    throw new RuntimeException("TODO: implement me");
  }

</#if>
<#if entity.canList>
  @GetMapping(value = "/todo-fix", produces = "application/json")
  public List<${entity.name.upperCamel}> list() {
    ${request.jvmView.unqualifiedContextClass} context = null;

    throw new RuntimeException("TODO: implement me");
  }

</#if>
<#if entity.canUpdate>
  @PutMapping(value = "/todo-fix", produces = "application/json")
  public void update(
    HttpServletRequest request) {
    //TODO: Update
  }

  <#list entity.patchableFields as field>
  @PatchMapping(value = "/todo-fix", produces = "application/json")
  public void set${field.name.upperCamel}(
    HttpServletRequest request
    ) {
    //TODO: Patch
  }

  </#list>
</#if>
//TODO: @PathVariable (for id fields)  ${entity.idFields[0].name.lowerCamel}, ${entity.idFields[0].java8View.typeLiteral}
//TODO: add tracing
//TODO: tracing: header parsing belongs in interceptor
//TODO: tracing: start tracing from the interceptor
//TODO: @ResponseBody
}
