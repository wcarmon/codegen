package com.wcarmon.codegen.model

/**
 * Represents ...
 * - REST: resource property/attribute
 * - Protocol buffer: field
 * - RDBMS: column
 *
 * - Kotlin: class field
 * - Java: class field, record field
 * - Golang: struct field
 * - Rust: struct field
 * - c: struct member
 * - c++: struct member, class data member
 * - Typescript: property
 */
data class Field(
  val name: Name,

  // TODO: type (primitive, built-in, custom, an Entity, enum, ...)

  val defaultValue: String? = null,
  val documentation: Documentation = Documentation.EMPTY,
  val enumType: Boolean = false,
  val nullable: Boolean = false,
  val rdbms: RDBMSColumn? = null,
  val validation: FieldValidation = FieldValidation(),
)
