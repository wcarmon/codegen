package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.ast.SQLDelightColumnDeclarationExpression
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage.SQL_DELIGHT

class SQLDelightColumnView(
  private val debugMode: Boolean,
  private val field: Field,
) {

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    targetLanguage = SQL_DELIGHT,
    terminate = false
  )

  val columnDefinition: String by lazy {
    SQLDelightColumnDeclarationExpression(field)
      .render(renderConfig)
  }
}
