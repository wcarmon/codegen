// see /home/wcarmon/git-repos/modern-jvm/trading-dao-jdbc/src/main/kotlin/com/wcarmon/trading/dao/rowmapper/convert.kt

package ${request.packageName.value}

<#list entity.kotlinView.importsForFields as importable>
import ${importable}
</#list>
import org.springframework.jdbc.core.RowMapper
<#if entity.jvmView.requiresObjectReader>
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.type.TypeReference
</#if>
<#list request.extraJVMImports as importable>
import ${importable}
</#list>

import java.sql.ResultSet
import java.time.format.DateTimeFormatter


/**
 * Maps one row of ResultSet data to ${entity.name.upperCamel} instance
 *
 * See https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/RowMapper.html
 */
<#if entity.jvmView.requiresObjectReader>
<#-- @SuppressWarnings("unchecked")-->
</#if>
class ${entity.name.upperCamel}RowMapper(
<#if entity.jvmView.requiresObjectReader>
  private val objectMapper: ObjectMapper,
</#if>
) : RowMapper<${entity.name.upperCamel}> {

  companion object {
    <#list entity.collectionFields as field>
    private val ${field.name.upperSnake}_TYPE_REF =
      object : TypeReference<${field.jvmView.jacksonTypeRef}> () {}
    </#list>
  }

  /**
   * Maps ${entity.fields?size}-fields from ResultSet
   *
   * @return ${entity.name.upperCamel} instance
   */
  override fun mapRow(rs: ResultSet, rowNum: Int): ${entity.name.upperCamel} =
    ${entity.name.upperCamel}(
      <#if !entity.idFields?has_content>
      // -- ${entity.commentForPKFields}
      </#if>
      <#list entity.idFields as field>
      ${field.name.lowerCamel} = ${field.kotlinView.resultSetGetterExpression},
      </#list>

      // -- Other Fields
      <#list entity.nonIdFields as field>
      ${field.name.lowerCamel} = ${field.kotlinView.resultSetGetterExpression},
      </#list>
    )


  <#if entity.jvmView.requiresObjectReader>
  /**
   * Deserialize to a java.util.List
   *
   * @param serialized json version of list data
   * @param <L> complete type, (including the List)
   * @return a new List (possibly empty, never null)
   */
  @Suppress("UnusedPrivateMember")
  private fun <T> toList(serialized: String?, typeRef: TypeReference<T>): T {
    if( serialized == null || serialized.trim().isEmpty() ) {
      @Suppress("UNCHECKED_CAST")
      return listOf<Any>() as T
    }

    return objectMapper.readValue(serialized, typeRef)
  }

  /**
   * Deserialize to a java.util.Set
   *
   * @param serialized json version of set data
   * @param <S> complete type, (including the Set)
   * @return a new Set (possibly empty, never null)
   */
  @Suppress("UnusedPrivateMember")
  private fun <T> toSet(serialized: String?, typeRef: TypeReference<T>): T {
    if( serialized == null || serialized.trim().isEmpty() ) {
      @Suppress("UNCHECKED_CAST")
      return setOf<Any>() as T
    }

    return objectMapper.readValue(serialized, typeRef)
  }
  </#if>
}
