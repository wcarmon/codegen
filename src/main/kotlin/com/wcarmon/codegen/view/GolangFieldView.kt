package com.wcarmon.codegen.view

import com.wcarmon.codegen.UPDATED_TS_FIELD_NAMES
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage

class GolangFieldView(
  private val debugMode: Boolean,
  private val field: Field,
  private val rdbmsView: RDBMSColumnView,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isGolang)
  }

  val typeLiteral: String = field.effectiveTypeLiteral(targetLanguage)

  val isUpdatedTimestamp: Boolean =
    field.effectiveBaseType(targetLanguage).isTemporal &&
        UPDATED_TS_FIELD_NAMES.any {
          field.name.lowerCamel.equals(it, true)
        }
}