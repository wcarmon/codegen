package ${request.packageName.value}
${request.golangView.templateDebugInfo}

${request.golangView.serializeImports(request.extraGolangImports)}

// -------------------------------------------------------------------
// SQL Queries
// Compatible with MariaDB, MySql, ...
// (Placeholders like ?, ?, ...)
// -------------------------------------------------------------------

<#list entities as entity>
  const (
<#if entity.hasIdFields>
      // Delete 1-row
      // - Entity: ${entity.name.upperCamel}
      // - PK column count: ${entity.idFields?size}
      DELETE__${entity.name.upperSnake} = `
      DELETE FROM ${entity.rdbmsView.qualifiedTableName}
      WHERE ${entity.rdbmsView.getPrimaryKeyWhereClause_questionMarks()}
      `

      // Test for existence of 1-row
      // - Entity: ${entity.name.upperCamel}
      // - PK column count: ${entity.idFields?size}
      ROW_EXISTS__${entity.name.upperSnake} = `
      SELECT COUNT(*)
      FROM ${entity.rdbmsView.qualifiedTableName}
      WHERE ${entity.rdbmsView.getPrimaryKeyWhereClause_questionMarks()}
      `

      // Find-by-PK
      // - Entity: ${entity.name.upperCamel}
      // - PK column count: ${entity.idFields?size}
      // - Columns count: ${entity.fields?size}
      FIND_BY_ID__${entity.name.upperSnake} = `
      SELECT ${entity.rdbmsView.commaSeparatedColumns}
      FROM ${entity.rdbmsView.qualifiedTableName}
      WHERE ${entity.rdbmsView.getPrimaryKeyWhereClause_questionMarks()}
      `
  <#if entity.hasNonIdFields>
      // Update 1-row
      // - Entity: ${entity.name.upperCamel}
      // - PK column count: ${entity.idFields?size}
      // - Columns count: ${entity.fields?size}
      UPDATE__${entity.name.upperSnake} = `
      UPDATE ${entity.rdbmsView.qualifiedTableName}
      SET
        ${entity.rdbmsView.updateSetClause_questionMarks}
      WHERE ${entity.rdbmsView.getPrimaryKeyWhereClause_questionMarks()}
      `
  </#if>
</#if>

  // Insert 1-row
  // - Entity: ${entity.name.upperCamel}
  // - PK column count: ${entity.idFields?size}
  // - Columns count: ${entity.fields?size}
  INSERT__${entity.name.upperSnake} = `
  INSERT INTO ${entity.rdbmsView.qualifiedTableName} (
    ${entity.rdbmsView.commaSeparatedColumns}
  )
  VALUES (${entity.rdbmsView.getQuestionMarkStringForInsert()})
  `

  // Select all rows
  // - Entity: ${entity.name.upperCamel}
  // - Columns count: ${entity.fields?size}
  SELECT_ALL__${entity.name.upperSnake} = `
  SELECT ${entity.rdbmsView.commaSeparatedColumns}
  FROM ${entity.rdbmsView.qualifiedTableName}
  `


    <#if entity.hasIdFields>
        ${entity.golangView.patchQueries_questionMark()}
    </#if>
  )

</#list>
