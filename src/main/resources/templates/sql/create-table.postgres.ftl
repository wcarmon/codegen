${request.jvmView.templateDebugInfo}
-- See https://www.postgresql.org/docs/current/sql-createtable.html
-- See https://www.postgresql.org/docs/current/datatype.html
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
CREATE TABLE IF NOT EXISTS ${entity.rdbmsView.schemaPrefix}${entity.name.lowerSnake}
(
  <#list entity.idFields as field>
    ${field.rdbmsView.postgresqlColumnDefinition}<#if field?has_next || entity.hasNonIdFields>,</#if>
  </#list>
  <#list entity.nonIdFields as field>
    ${field.rdbmsView.postgresqlColumnDefinition}<#if field?has_next>,</#if>
  </#list>
  ${entity.rdbmsView.constraints}
);
</#list>
