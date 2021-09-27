package com.wcarmon.codegen.view

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
}
