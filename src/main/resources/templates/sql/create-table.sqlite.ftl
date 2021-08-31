${request.jvmView.templateNameComment}
-- See https://www.sqlite.org/lang_createtable.html
-- See https://www.sqlite.org/datatype3.html
<#-- -->
<#list entities as entity>

-- Entity: ${entity.pkg.value}.${entity.name.upperCamel}
-- PK column count: ${entity.idFields?size}
-- Columns count: ${entity.fields?size}
CREATE TABLE IF NOT EXISTS ${entity.rdbmsView.schemaPrefix}${entity.name.lowerSnake}
(
  <#list entity.idFields as field>
    ${field.rdbmsView.sqliteColumnDefinition}<#if field?has_next || entity.hasNonIdFields>,</#if>
  </#list>
  <#list entity.nonIdFields as field>
    ${field.rdbmsView.sqliteColumnDefinition}<#if field?has_next>,</#if>
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
