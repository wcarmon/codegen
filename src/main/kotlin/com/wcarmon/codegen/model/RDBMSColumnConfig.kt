package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import org.apache.logging.log4j.LogManager

private const val MAX_TYPE_LITERAL_LENGTH = 64


/**
 * [Field] attributes specific to relational database column
 *
 * See src/main/resources/json-schema/rdbms-column.schema.json
 */
@JsonPropertyOrder(alphabetic = true)
data class RDBMSColumnConfig(

  val autoIncrement: Boolean = false,

  val varcharLength: Int? = null,

  /**
   * Replace the auto-derived type with this literal
   */
  val overrideEffectiveType: String = "",

  /**
   * For VARCHAR columns, wrap in single quotes
   */
  val defaultValue: DefaultValue = DefaultValue(),

  @JsonProperty("validation")
  val validationConfig: FieldValidation = FieldValidation(),

  //TODO: represent foreign Keys
) {

  companion object {
    @JvmStatic
    private val LOG = LogManager.getLogger(RDBMSColumnConfig::class.java)
  }

  init {
    if (varcharLength != null) {
      require(varcharLength > 0) { "varcharLength must be positive: this=$this" }
    }

    if (overrideEffectiveType.isNotBlank()) {
      require(overrideEffectiveType.length < MAX_TYPE_LITERAL_LENGTH) {
        "overrideEffectiveType too long: $overrideEffectiveType, this=$this"
      }

      // yes you can ;-)
//      require(varcharLength == null) {
//        "Cannot set varchar length when overriding type: this=$this"
//      }
    }

    //TODO: warn in situations where varcharLength will be ignored
  }

  val overrideBaseType: BaseFieldType? =
    if (overrideEffectiveType.isBlank()) null
    else BaseFieldType.parse(overrideEffectiveType)
}
