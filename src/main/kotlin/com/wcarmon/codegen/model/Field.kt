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

  val type: LogicalFieldType,

  val defaultValue: String? = null,

  val documentation: Documentation = Documentation.EMPTY,

  val rdbms: RDBMSColumn? = null,


  // --- These are part of the type -----------
  /** Bounded set of acceptable values? */
  val enumType: Boolean = false,  // part of validation?

  val validation: FieldValidation = FieldValidation(),
)
