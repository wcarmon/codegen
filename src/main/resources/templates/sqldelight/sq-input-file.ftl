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

<#-- TODO: Delete -->

<#-- TODO: Exists/Contains -->

<#-- TODO: FindById -->

<#-- TODO: Insert -->

<#-- TODO: Select-all -->

<#-- TODO: Update -->

<#-- TODO: Patch -->
-- ==========================================================
<#--


CREATE INDEX boardRecord_horizon ON boardRecord (horizon);
CREATE INDEX boardRecord_created ON boardRecord (created_at);
CREATE INDEX boardRecord_importance ON boardRecord (importance);
CREATE INDEX boardRecord_name ON boardRecord (name);
CREATE INDEX boardRecord_updated ON boardRecord (updated_at);


selectAll:
SELECT *
FROM boardRecord
ORDER BY name;


findById:
SELECT *
FROM boardRecord
WHERE uuid = ?;


contains:
SELECT COUNT(1)
FROM boardRecord
WHERE uuid = ?;


insert:
INSERT INTO boardRecord(
uuid,
background_image,
closed,
created_at,
details,
enable_search,
horizon,
icon_code,
importance,
keywords,
name,
pinned_position,
tags,
theme_color,
updated_at
)
VALUES ?;


update:
UPDATE boardRecord
SET
background_image = ?,
closed = ?,
details = ?,
enable_search = ?,
horizon = ?,
icon_code = ?,
importance = ?,
keywords = ?,
name = ?,
pinned_position = ?,
tags = ?,
updated_at = ?,
theme_color = ?
WHERE uuid = ?;


delete:
DELETE
FROM boardRecord
WHERE uuid = ?;


setBackgroundImage:
UPDATE boardRecord
SET background_image = ?,
updated_at = ?
WHERE uuid = ?;


setClosed:
UPDATE boardRecord
SET closed = ?,
updated_at = ?
WHERE uuid = ?;


setDetails:
UPDATE boardRecord
SET details = ?,
updated_at = ?
WHERE uuid = ?;


setEnableSearch:
UPDATE boardRecord
SET enable_search = ?,
updated_at = ?
WHERE uuid = ?;


setIconCode:
UPDATE boardRecord
SET icon_code = ?,
updated_at = ?
WHERE uuid = ?;


setName:
UPDATE boardRecord
SET name = ?,
updated_at = ?
WHERE uuid = ?;


setHorizon:
UPDATE boardRecord
SET horizon = ?,
updated_at = ?
WHERE uuid = ?;


setImportance:
UPDATE boardRecord
SET importance = ?,
updated_at = ?
WHERE uuid = ?;


setKeywords:
UPDATE boardRecord
SET keywords = ?,
updated_at = ?
WHERE uuid = ?;


setPinnedPosition:
UPDATE boardRecord
SET pinned_position = ?,
updated_at = ?
WHERE uuid = ?;


setTags:
UPDATE boardRecord
SET tags = ?,
updated_at = ?
WHERE uuid = ?;


setThemeColor:
UPDATE boardRecord
SET theme_color = ?,
updated_at = ?
WHERE uuid = ?;



CREATE TABLE attachmentTaskJoin(
attachment_uuid  TEXT  NOT NULL,
task_uuid        TEXT  NOT NULL,
UNIQUE( attachment_uuid, task_uuid )
);


selectAll:
SELECT *
FROM attachmentTaskJoin;


selectForAttachment:
SELECT *
FROM attachmentTaskJoin
WHERE attachment_uuid = ?;


selectForTask:
SELECT *
FROM attachmentTaskJoin
WHERE task_uuid = ?;

-->
