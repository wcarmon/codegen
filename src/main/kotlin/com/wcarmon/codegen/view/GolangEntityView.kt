package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.TargetLanguage

class GolangEntityView(
  private val debugMode: Boolean,
  private val entity: Entity,
  private val rdbmsView: RDBMSTableView,
  private val targetLanguage: TargetLanguage,
) {


  fun patchQueries_questionMark(): String {
    TODO()
  }

  fun patchQueries_numberdDollar(): String {
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
        lines += """${indentation}SET ${field.name.lowerSnake}=$1 """

        var pkIndexOffset = 2 // 1 for patched field
        if (entity.updatedTimestampFieldName != null && !field.golangView.isUpdatedTimestamp) {
          lines += """${indentation}  AND ${entity.updatedTimestampFieldName.lowerSnake}=$2"""
          pkIndexOffset++
        }

        lines += """${indentation}WHERE ${entity.rdbmsView.primaryKeyWhereClause_numberedDollars(pkIndexOffset)}"""
        lines += "`"

        lines.joinToString(
          separator = "\n"
        ) {
          "$indentation$it"
        }

      }.joinToString(
        separator = "\n\n"
      ) {
        "$indentation$it"
      }
  }
}
