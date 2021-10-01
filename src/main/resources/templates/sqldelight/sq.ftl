<#--
NOTE:
SQLDelight writes to build/generated/sqldelight/code/<DatabaseNameInBuildDotGradle>/com/foo/FooRecord.kt

Generate:
./gradlew :sandbox:generateMain<DatabaseName>Interface
./gradlew :sandbox:generateMainSandboxDatabaseInterface
./gradlew :sandbox build -x test

(TODO: intellij build doesn't generate *.sq -> kotlin)
-->
-- See https://cashapp.github.io/sqldelight/jvm_sqlite/types/
${entity.sqlDelightView.createTableStatement}

${entity.sqlDelightView.createIndexStatements}


contains:
SELECT COUNT(1)
FROM ${entity.name.lowerCamel}Record
${entity.sqlDelightView.whereClauseForIdFields};


delete:
DELETE
FROM ${entity.name.lowerCamel}Record
${entity.sqlDelightView.whereClauseForIdFields};


findById:
SELECT *
FROM ${entity.name.lowerCamel}Record
${entity.sqlDelightView.whereClauseForIdFields};


insert:
${entity.sqlDelightView.insertQuery}


selectAll:
SELECT *
FROM ${entity.name.lowerCamel}Record;


<#if entity.hasNonIdFields>
update:
UPDATE ${entity.name.lowerCamel}Record
SET
${entity.sqlDelightView.placeholderColumnSetters("  ")}
${entity.sqlDelightView.whereClauseForIdFields};
</#if>

<#-- TODO: Allow other select queries in json -->

<#list entity.patchableFields as field>
set${field.name.upperCamel}:
${entity.sqlDelightView.patchQuery(field)}

</#list>
