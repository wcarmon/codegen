-- See https://mariadb.com/kb/en/create-table/
-- See https://mariadb.com/kb/en/data-types/
-- See ${request.prettyTemplateName}
<#-- -->
SET sql_mode='ANSI_QUOTES';

-- TODO: add a USE <dbname> statement here
  <#list entities as entity>

  -- Entity: ${entity.pkg.value}.${entity.name.upperCamel}
  -- PK column count: ${entity.primaryKeyFields?size}
  -- Columns count: ${entity.fields?size}
  CREATE TABLE IF NOT EXISTS ${entity.dbSchemaPrefix}${entity.name.lowerSnake}
  (
    <#list entity.primaryKeyFields as field>
    <#-- TODO: fix types -->
        ${field.postgresqlColumnDefinition}<#if field?has_next || entity.hasNonPrimaryKeyFields>,</#if>
    </#list>
    <#list entity.nonPrimaryKeyFields as field>
        ${field.postgresqlColumnDefinition}<#if field?has_next>,</#if>
    </#list>
    <#--  -->
    <#if entity.hasPrimaryKeyFields>
      ,
        ${entity.primaryKeyTableConstraint}
    </#if>
    <#-- TODO: unique constraints -->
    <#-- TODO: check constraints -->
  );
</#list>
