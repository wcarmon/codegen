package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.golangTypeLiteral

class GolangFieldView(
  private val debugMode: Boolean,
  private val field: Field,
  private val rdbmsView: RDBMSColumnView,
  private val targetLanguage: TargetLanguage,
) {

  val typeLiteral: String = golangTypeLiteral(field)
}
