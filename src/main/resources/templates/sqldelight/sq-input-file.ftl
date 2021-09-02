<#--
NOTE:
SQLDelight writes to build/generated/sqldelight/code/<DatabaseNameInBuildDotGradle>/com/foo/FooRecord.kt

Generate:
./gradlew :generateMain<DatabaseName>Interface
./gradlew :sandbox:generateMainSandboxDatabaseInterface
./gradlew build -x test

(TODO: intellij build doesn't generate *.sq -> kotlin)
-->
-- See https://cashapp.github.io/sqldelight/jvm_sqlite/types/
CREATE TABLE ${entity.name.lowerCamel}Record
(
${entity.sqlDelightView.columnDefinitions}
);

<#-- TODO: Indexes -->

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


update:
UPDATE ${entity.name.lowerCamel}Record
SET
${entity.sqlDelightView.placeholderColumnSetters}
${entity.sqlDelightView.whereClauseForIdFields};


<#-- TODO: Allow other select queries in json -->

<#list entity.patchableFields as field>
set${field.name.upperCamel}:
${entity.sqlDelightView.patchQuery(field)}

</#list>
<#--
CREATE INDEX ${entity.name.lowerCamel}Record_horizon    ON ${entity.name.lowerCamel}Record (horizon);
CREATE INDEX ${entity.name.lowerCamel}Record_created    ON ${entity.name.lowerCamel}Record (created_at);
CREATE INDEX ${entity.name.lowerCamel}Record_importance ON ${entity.name.lowerCamel}Record (importance);
CREATE INDEX ${entity.name.lowerCamel}Record_name       ON ${entity.name.lowerCamel}Record (name);
CREATE INDEX ${entity.name.lowerCamel}Record_updated    ON ${entity.name.lowerCamel}Record (updated_at);



CREATE TABLE attachmentTaskJoin(
attachment_uuid  TEXT  NOT NULL,
task_uuid        TEXT  NOT NULL,
UNIQUE( attachment_uuid, task_uuid )
);




selectForAttachment:
SELECT *
FROM attachmentTaskJoin
WHERE attachment_uuid = ?;


selectForTask:
SELECT *
FROM attachmentTaskJoin
WHERE task_uuid = ?;

-->
