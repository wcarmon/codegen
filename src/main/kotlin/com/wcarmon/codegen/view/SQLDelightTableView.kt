package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
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

  val whereClauseForIdFields: String by lazy {

    entity.idFields.joinToString(
      prefix = "WHERE ",
      separator = " AND ") {
      "${it.name.lowerSnake}=?"
    }
  }

  val insertQuery: String by lazy {

    val indentation = "  "

    entity.sortedFieldsWithIdsFirst
      .joinToString(
        prefix = "INSERT INTO ${entity.name.lowerCamel}Record (\n$indentation",
        separator = ",\n$indentation",
        postfix = "\n) VALUES ?;",
      ) {
        it.name.lowerSnake
      }
  }

  val placeholderColumnSetters: String by lazy {
    val indentation = "  "

    entity.nonIdFields
      .joinToString(
        prefix = indentation,
        separator = ",\n$indentation",
      ) {
        it.name.lowerSnake + "=?"
      }
  }

  fun patchQuery(field: Field): String {
    val output = StringBuilder(256)
    output.append("UPDATE ${entity.name.lowerCamel}Record\n")
    output.append("SET ${field.name.lowerSnake}=?")

    if (
      entity.updatedTimestampFieldName != null &&
      entity.updatedTimestampFieldName != field.name
    ) {
      output.append(", ")
      output.append(entity.updatedTimestampFieldName.lowerSnake)
      output.append("=?")
    }

    output.append("\n")
    output.append(entity.sqlDelightView.whereClauseForIdFields)
    output.append(";")

    return output.toString()
  }
}
