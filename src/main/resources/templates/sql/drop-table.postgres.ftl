${request.jvmView.templateDebugInfo}
<#list entities as entity>
DROP TABLE IF EXISTS ${entity.rdbmsView.schemaPrefix}${entity.name.lowerSnake};
</#list>
