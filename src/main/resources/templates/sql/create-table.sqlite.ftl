${request.jvmView.templateDebugInfo}
-- See https://www.sqlite.org/lang_createtable.html
-- See https://www.sqlite.org/datatype3.html
<#-- -->

-- ** Drop
<#list entities as entity>
--  DROP TABLE IF EXISTS ${entity.rdbmsView.schemaPrefix}${entity.name.lowerSnake};
</#list>

-- ** Create
<#list entities as entity>

-- Entity: ${entity.pkg.value}.${entity.name.upperCamel}
-- PK column count: ${entity.idFields?size}
-- Columns count: ${entity.fields?size}
CREATE TABLE IF NOT EXISTS ${entity.name.lowerSnake}
(
  <#list entity.idFields as field>
    ${field.rdbmsView.sqliteColumnDefinition}<#if field?has_next || entity.hasNonIdFields>,</#if>
  </#list>
  <#list entity.nonIdFields as field>
    ${field.rdbmsView.sqliteColumnDefinition}<#if field?has_next>,</#if>
  </#list>
  ${entity.rdbmsView.constraints}
);
</#list>
