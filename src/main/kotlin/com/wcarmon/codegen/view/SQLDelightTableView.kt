package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.TargetLanguage.SQL_DELIGHT

class SQLDelightTableView(
  private val debugMode: Boolean,
  private val entity: Entity,
) {

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    targetLanguage = SQL_DELIGHT,
    terminate = false
  )

  val columnDefinitions: String by lazy {
    val indentation = "  "

    entity.sortedFieldsWithIdsFirst
      .map {
        it.sqlDelightView.columnDefinition
      }
      .joinToString(
        prefix = indentation,
        separator = ",\n${indentation}") {
        it.trimEnd()
      }
  }
}
