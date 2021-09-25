package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.TargetLanguage

class GolangEntityView(
  private val debugMode: Boolean,
  private val entity: Entity,
  private val rdbmsView: RDBMSTableView,
  private val targetLanguage: TargetLanguage,
)
