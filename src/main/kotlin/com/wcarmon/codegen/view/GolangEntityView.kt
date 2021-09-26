package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.FinalityModifier.FINAL
import com.wcarmon.codegen.ast.MethodParameterExpression
import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.SQLPlaceholderType
import com.wcarmon.codegen.model.SQLPlaceholderType.*
import com.wcarmon.codegen.model.TargetLanguage

class GolangEntityView(
  private val debugMode: Boolean,
  private val entity: Entity,
  private val rdbmsView: RDBMSTableView,
  private val targetLanguage: TargetLanguage,
) {

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    lineIndentation = "",
    targetLanguage = targetLanguage,
    terminate = false,
  )

  val commaSeparatedIdFields = entity
    .idFields
    .joinToString(",") {
      it.name.lowerCamel
    }

  fun patchQueries_questionMark(): String = patchQueries(QUESTION_MARK)

  fun patchQueries_numberdDollar(): String = patchQueries(NUMBERED_DOLLARS)

  fun methodArgsForIdFields() =
    entity.idFields.joinToString(
      separator = ", "
    ) { field ->
      MethodParameterExpression(
        field = field,
        finalityModifier = FINAL,
        fullyQualified = false,
      )
        .render(renderConfig)
    }

  private fun patchQueries(placeholderType: SQLPlaceholderType): String {
    val indentation = "    "

    if (!entity.hasIdFields || !entity.hasNonIdFields) {
      // need ID fields for WHERE clause
      return ""
    }

    if (!entity.canUpdate) {
      return ""
    }

    return entity.nonIdFields
      .map { field ->
        val lines = mutableListOf<String>()

        lines += "// Patch ${entity.name.upperCamel}.${field.name.lowerCamel}"
        lines += "PATCH__${entity.name.upperSnake}__${field.name.upperSnake} = `"
        lines += """${indentation}UPDATE "${entity.name.lowerSnake}" """

        val p = placeholderType.firstPlaceholder()
        lines += """${indentation}SET ${field.name.lowerSnake}=$p """

        var pkIndexOffset = 2 // 1 for patched field

        if (entity.updatedTimestampFieldName != null && !field.golangView.isUpdatedTimestamp) {
          val placeholder = placeholderType.forIndex(2)
          lines += """${indentation}  AND ${entity.updatedTimestampFieldName.lowerSnake}=$placeholder"""

          pkIndexOffset++
        }

        val whereClause = when (placeholderType) {
          NAMED_PARAMS -> TODO()
          NUMBERED_DOLLARS -> rdbmsView.primaryKeyWhereClause_numberedDollars(pkIndexOffset)
          QUESTION_MARK -> rdbmsView.primaryKeyWhereClause_questionMarks
        }

        lines += """${indentation}WHERE $whereClause"""
        lines += "`"

        lines.joinToString(
          separator = "\n"
        ) {
          "$indentation$it"
        }

      }.joinToString(
        separator = "\n\n"
      ) { line ->
        "$indentation$line"
      }
  }
}
