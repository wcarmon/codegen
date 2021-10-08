${request.jvmView.templateDebugInfo}
<#list entities as entity>
DROP TABLE IF EXISTS ${entity.name.lowerSnake};
</#list>
