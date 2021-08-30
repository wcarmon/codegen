-- See https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-TABLE.html#GUID-F9CE0CC3-13AE-4744-A43C-EAC7A71AAAB6
-- See https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/Data-Types.html#GUID-A3C0D836-BADB-44E5-A5D4-265BA5968483
-- See ${request.prettyTemplateName}
<#-- -->
<#list entities as entity>

-- Entity: ${entity.pkg.value}.${entity.name.upperCamel}
-- PK column count: ${entity.idFields?size}
-- Columns count: ${entity.fields?size}
  CREATE TABLE IF NOT EXISTS ${entity.rdbmsView.schemaPrefix}${entity.name.lowerSnake}
  (
    <#list entity.idFields as field>
    <#-- TODO: fix types -->
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
