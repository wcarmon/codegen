${request.jvmView.templateNameComment}
-- See https://www.postgresql.org/docs/current/sql-createtable.html
-- See https://www.postgresql.org/docs/current/datatype.html
<#-- -->
<#list entities as entity>

-- Entity: ${entity.pkg.value}.${entity.name.upperCamel}
-- PK column count: ${entity.idFields?size}
-- Columns count: ${entity.fields?size}
CREATE TABLE IF NOT EXISTS ${entity.rdbmsView.schemaPrefix}${entity.name.lowerSnake}
(
  <#list entity.idFields as field>
    ${field.rdbmsView.postgresqlColumnDefinition}<#if field?has_next || entity.hasNonIdFields>,</#if>
  </#list>
  <#list entity.nonIdFields as field>
    ${field.rdbmsView.postgresqlColumnDefinition}<#if field?has_next>,</#if>
  </#list>
<#--  -->
  <#if entity.hasIdFields>
  ,
    ${entity.rdbmsView.primaryKeyTableConstraint}
  </#if>
  <#-- TODO: unique constraints -->
  <#-- TODO: check constraints -->
);
</#list>
