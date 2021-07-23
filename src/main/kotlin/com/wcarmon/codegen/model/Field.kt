package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder

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
@JsonPropertyOrder(alphabetic = true)
data class Field(
  val name: Name,

  // TODO: type (primitive, built-in, custom, an Entity, enum, ...)

  val defaultValue: String? = null,

  val documentation: Documentation = Documentation.EMPTY,

  /** Bounded set of acceptable values? */
  val enumType: Boolean = false,

  val nullable: Boolean = false,

  val rdbms: RDBMSColumn? = null,

  val validation: FieldValidation = FieldValidation(),
)
