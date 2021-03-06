package com.wcarmon.codegen

/**
 * Enables/Disables Tracing/debug comments in generated code
 */
//TODO: make this a command line flag
const val DEBUG_MODE = false

/**
 * For Identifiers
 */
const val MAX_NAME_LENGTH = 64

/**
 * Official docs on patterns: https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 *
 * Entity files are in json files.
 * Multiple are supported.
 *
 * Files names look like: *.entity.json
 * (eg. "foo.entity.json")
 */
const val PATTERN_FOR_ENTITY_FILE = "glob:**/*.entity.json"


/**
 * Official docs on patterns: https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 *
 * Code gen requests are in json files.
 * Multiple are supported.
 *
 * Files names look like: *.request.json
 * (eg. "row-mappers.request.json")
 */
const val PATTERN_FOR_GEN_REQ_FILE = "glob:**/*.request.json"

/** Freemarker */
const val TEMPLATE_SUFFIX = ".ftl"

/** Velocity */
//const val TEMPLATE_SUFFIX = ".vm"


/**
 * Names for timestamp field
 * Used to auto set a value on entity creation
 * Uses camelCase
 */
@Deprecated("just compare entity::createdTimestampFieldName")
val CREATED_TS_FIELD_NAMES = setOf("created", "createdAt", "createdOn")

/**
 * Names for timestamp field
 * Used to auto set a value on entity update
 * Uses lower camelCase
 */
@Deprecated("just compare entity::updatedTimestampFieldName")
val UPDATED_TS_FIELD_NAMES = setOf(
  "updated", "updatedAt", "updatedOn"
)
