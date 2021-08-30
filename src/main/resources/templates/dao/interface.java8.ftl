package ${request.packageName.value};

${request.java8View.serializeImports(
  entity.java8View.importsForFields,
  request.extraJVMImports,
  request.jvmContextClass)}


/**
 * DAO Contract for {@link ${entity.pkg.value}.${entity.name.upperCamel}}
 * PK field count: ${entity.idFields?size}
 * Field count: ${entity.fields?size}
 *
 * Implementations must be ThreadSafe
 * See: ${request.prettyTemplateName}
 */
public interface ${entity.name.upperCamel}DAO {

  <#if entity.hasIdFields>
  /**
   * Delete at-most-one existing {@link ${entity.pkg.value}.${entity.name.upperCamel}} instance
   * <p>
   * NOOP if no matching entity exists
   *
   * @param TODO
   */
  <#-- TODO: Add @param to javadoc for context (dynamic) -->
  void delete(${request.jvmView.unqualifiedContextClass} context, ${entity.java8View.methodArgsForIdFields(false)});

  /**
   * @param TODO
   * @return true when {@link ${entity.pkg.value}.${entity.name.upperCamel}} exists with matching PK
   */
<#--  TODO: Add @param to javadoc for context (dynamic) &ndash;&gt;-->
  boolean exists(${request.jvmView.unqualifiedContextClass} context, ${entity.java8View.methodArgsForIdFields(false)});

  /**
   * @param TODO
   * @return one {@link ${entity.pkg.value}.${entity.name.upperCamel}} instance (matching PKs) or null
   */
<#--  TODO: Add @param to javadoc for context (dynamic) &ndash;&gt;-->
  ${entity.name.upperCamel} findById(${request.jvmView.unqualifiedContextClass} context, ${entity.java8View.methodArgsForIdFields(false)});

  </#if>

  /**
   * Create at-most-one {@link ${entity.pkg.value}.${entity.name.upperCamel}} instance
   */
  void create(${request.jvmView.unqualifiedContextClass} context, ${entity.name.upperCamel} entity);

  /**
   * @return all {@link ${entity.pkg.value}.${entity.name.upperCamel}} entities or empty List (never null)
   */
<#--  TODO: Add @param to javadoc for context -->
  List<${entity.name.upperCamel}> list(${request.jvmView.unqualifiedContextClass} context);

  /**
   * Update all (non-PK) fields on one {@link ${entity.pkg.value}.${entity.name.upperCamel}} instance
   * (${entity.nonIdFields?size} non-PK fields)
   */
<#--  TODO: Add @param to javadoc for context -->
  void update(${request.jvmView.unqualifiedContextClass} context, ${entity.name.upperCamel} entity);

  /**
   * Upsert/Put {@link ${entity.pkg.value}.${entity.name.upperCamel}}
   * <p>
   * Update if entity exists, Create if entity does not exist
   * <p>
   * Same concept as {@link java.util.Map#put}
   *
   * @param entity to update or create
   */
<#--  TODO: Add @param to javadoc for context -->
  void upsert(${request.jvmView.unqualifiedContextClass} context, ${entity.name.upperCamel} entity);

  <#list entity.nonIdFields as field>
  /**
   * Patch/Set
   * <p>
   * Set one field: {@link ${entity.pkg.value}.${entity.name.upperCamel}#${field.name.lowerCamel}}
   *
   * @param ${field.name.lowerCamel} - replacement for existing value
   */
<#--  TODO: Add @param to javadoc for context -->
  void set${field.name.upperCamel}(
    ${request.jvmView.unqualifiedContextClass} context,
    ${entity.java8View.methodArgsForIdFields(false)},
    ${field.java8View.unqualifiedType} ${field.name.lowerCamel});

    </#list>
}
