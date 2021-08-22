package ${request.packageName.value}

<#if request.jvmContextClass?has_content>
import ${request.jvmContextClass}
</#if>
<#list request.extraJVMImports as importable>
import ${importable}
</#list>
<#list entity.kotlinImportsForFields as importable>
import ${importable}
</#list>


/**
 * DAO Contract for [${entity.pkg.value}.${entity.name.upperCamel}]
 * PK field count: ${entity.primaryKeyFields?size}
 * Field count: ${entity.fields?size}
 *
 * Implementations must be ThreadSafe
 * See: ${request.prettyTemplateName}
 */
@Suppress("TooManyFunctions")
interface ${entity.name.upperCamel}DAO {

<#if entity.hasPrimaryKeyFields>
 /**
  * Delete at-most-one existing {@link ${entity.pkg.value}.${entity.name.upperCamel}} instance
  *
  * NOOP if no matching entity exists
  *
  * @param TODO
  */
<#-- TODO: Add @param to javadoc for context -->
  fun delete(context: ${request.unqualifiedContextClass}, ${entity.kotlinMethodArgsForPKFields(false)})

  /**
  * @param TODO
  * @return true when {@link ${entity.pkg.value}.${entity.name.upperCamel}} exists with matching PK
  */
<#-- TODO: Add @param to kotlindoc for context  -->
  fun exists(context: ${request.unqualifiedContextClass}, ${entity.kotlinMethodArgsForPKFields(false)}): Boolean

  /**
  * @param TODO
  * @return one {@link ${entity.pkg.value}.${entity.name.upperCamel}} instance (matching PKs) or null
  */
  <#-- TODO: Add @param to kotlindoc for context  -->
  fun findById( context: ${request.unqualifiedContextClass}, ${entity.kotlinMethodArgsForPKFields(false)}): ${entity.name.upperCamel}?
</#if>

  /**
  * Create at-most-one {@link ${entity.pkg.value}.${entity.name.upperCamel}} instance
  */
  fun create(context: ${request.unqualifiedContextClass}, entity: ${entity.name.upperCamel})

  /**
  * @return all {@link ${entity.pkg.value}.${entity.name.upperCamel}} entities or empty List (never null)
  */
<#-- TODO: Add @param to kotlindoc for context  -->
  fun list(context: ${request.unqualifiedContextClass}): List<${entity.name.upperCamel}>

  /**
  * Update all (non-PK) fields on one {@link ${entity.pkg.value}.${entity.name.upperCamel}} instance
  * (${entity.nonPrimaryKeyFields?size} non-PK fields)
  */
<#-- TODO: Add @param to kotlindoc for context  -->
  fun update(context: ${request.unqualifiedContextClass}, entity: ${entity.name.upperCamel})

  /**
  * Upsert/Put {@link ${entity.pkg.value}.${entity.name.upperCamel}}
  *
  * Update if entity exists, Create if entity does not exist
  *
  * Same concept as {@link java.util.Map#put}
  *
  * @param entity to update or create
  */
<#-- TODO: Add @param to kotlindoc for context  -->
  fun upsert(context: ${request.unqualifiedContextClass}, entity: ${entity.name.upperCamel})

<#list entity.nonPrimaryKeyFields as field>
 /**
  * Patch/Set
  *
  * Set one field: {@link ${entity.pkg.value}.${entity.name.upperCamel}#${field.name.lowerCamel}}
  *
  * @param ${field.name.lowerCamel} - replacement for existing value
  */
  <#-- TODO: Add @param to kotlindoc for context  -->
  fun set${field.name.upperCamel}(context: ${request.unqualifiedContextClass},
    ${entity.kotlinMethodArgsForPKFields(false)},
    ${field.name.lowerCamel}: ${field.unqualifiedKotlinType})

</#list>
}
