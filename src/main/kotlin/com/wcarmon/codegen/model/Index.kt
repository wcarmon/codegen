package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Ordered field/column names for fast retrieval
 *
 * See https://en.wikipedia.org/wiki/Database_index
 * See https://mariadb.com/kb/en/getting-started-with-indexes/
 * See https://www.postgresql.org/docs/current/indexes-intro.html
 * See https://www.sqlite.org/lang_createindex.html
 */
data class Index(
  @JsonValue
  val fieldNames: List<Name>,
) {
  //TODO: implement comparable

  companion object {

    @JvmStatic
    fun build(vararg names: String): Index =
      Index(names.map { Name(it) }.toList())

    @JvmStatic
    @JsonCreator
    fun build(fields: Collection<String>): Index =
      Index(fields.map {
        Name(it)
      }.toList())
  }

  init {
    require(fieldNames.isNotEmpty()) {
      "At least one field name is required on an index"
    }

    require(fieldNames.distinct().size == fieldNames.size) {
      "Field names must be unique: $fieldNames"
    }
  }

  val first: Name = fieldNames.first()
  val size: Int = fieldNames.size

}
