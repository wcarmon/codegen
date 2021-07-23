package com.wcarmon.codegen.model

/** Represents logical validations performed on field */
data class FieldValidation(

  // For Files
  val requireDirectory: Boolean = false,
  val requireDirectoryIfExists: Boolean = false,
  val requireRegularFile: Boolean = false,
  val requireRegularFileIfExists: Boolean = false,


  // For Strings, Collections, ...
  val maxSize: Int? = null,
  val minSize: Int? = null,


  // For Collections
  val requireItemsDistinct: Boolean = true,
  //  val requireItemsSorted: Boolean = false, complicated: by what property? reversed?


  // For Strings
  val requireLowerCase: Boolean = false,
  val requireMatchesRegex: Regex? = null,
  val requireNotBlank: Boolean = false,
  val requireTrimmed: Boolean = false,
  val requireUpperCase: Boolean = false,


  // For Numbers
  val coerceAtLeast: Number? = null, // coerce when outside range
  val coerceAtMost: Number? = null, // coerce when outside range
  val maxValue: Number? = null, // error when outside range
  val minValue: Number? = null, // error when outside range
) {

  //TODO: Date-like: after
  //TODO: Date-like: before
}
