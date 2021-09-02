package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.RawLiteralExpression
import com.wcarmon.codegen.ast.RenderConfig
import com.wcarmon.codegen.ast.WrapWithSerdeExpression
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.Index
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage.SQL_DELIGHT
import com.wcarmon.codegen.util.effectiveJDBCSerde

class SQLDelightTableView(
  private val debugMode: Boolean,
  private val entity: Entity,
) {

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    targetLanguage = SQL_DELIGHT,
    terminate = false
  )

  fun columnDefinitions(indentation: String): String =
    entity.sortedFieldsWithIdsFirst
      .map {
        it.sqlDelightView.columnDefinition
      }
      .joinToString(
        prefix = indentation,
        separator = ",\n${indentation}") {
        it.trimEnd()
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

  fun placeholderColumnSetters(
    indentation: String = "  ",
  ): String =
    entity.nonIdFields
      .joinToString(
        prefix = indentation,
        separator = ",\n$indentation",
      ) {
        it.name.lowerSnake + "=?"
      }

  fun uniqueConstraints(
    indentation: String,
  ): String {

    if (entity.indexes.isEmpty()) {
      return ""
    }

    return entity.indexes
      .joinToString(
        prefix = indentation,
        separator = ",\n$indentation"
      ) {
        buildIndexConstraint(it)
      }
  }

  val createIndexStatements: String by lazy {
    //TODO: sort the indexes first (using some sensible comparator)
    entity.indexes
      .mapIndexed { i, index ->
        val commaSeparatedFields = index
          .fieldNames
          .joinToString(", ") {
            it.lowerSnake
          }

        "CREATE INDEX ${entity.name.lowerCamel}Record_$i ON ${entity.name.lowerCamel}Record ($commaSeparatedFields);"

      }.joinToString(
        separator = "\n",
      )
  }

  val createTableStatement: String by lazy {
    //TODO: move to expression
    val indentation = "  "

    val output = StringBuilder(1024)
    output.append("CREATE TABLE ${entity.name.lowerCamel}Record\n")
    output.append("(\n")

    output.append(
      listOf(
        columnDefinitions(indentation),
        primaryKeyConstraint(indentation),
        uniqueConstraints(indentation),
      )
        .filter { it.isNotBlank() }
        .joinToString(",\n")
    )

    output.append("\n);")
    output.toString()
  }

  fun primaryKeyConstraint(indentation: String): String {
    if (!entity.hasIdFields) {
      return ""
    }

    return entity.idFields.joinToString(
      prefix = "${indentation}PRIMARY KEY(",
      separator = ",",
      postfix = ")",
    ) {
      it.name.lowerSnake
    }
  }

  fun patchQuery(field: Field): String {
    //TODO: move to expression
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

  val recordToModel: String by lazy {
    val indentation = "  "
    val recordId = "record"
    val returnType = entity.name.upperCamel

    val argsSegment = listOf(
      "record: ${entity.name.upperCamel}Record",
      if (entity.jvmView.requiresObjectReader) "objectMapper: ObjectMapper" else "",
    )
      .filter { it.isNotBlank() }
      .joinToString(separator = ", ")

    // ------------------------------------------------

    //TODO: move to expression
    val output = StringBuilder(1024)

    //TODO: [com.wcarmon.codegen.ast.MethodDeclarationExpression]
    output.append("fun toModel($argsSegment): $returnType =\n")
    output.append("$indentation$returnType(\n")

    entity.sortedFieldsWithIdsFirst.map { field ->

      val columnRead =
        if (field.type.nullable) "it"
        else "$recordId.${field.name.lowerSnake}"

      val serdeWrapped = WrapWithSerdeExpression(
        serde = effectiveJDBCSerde(field),
        serdeMode = DESERIALIZE,
        wrapped = RawLiteralExpression(columnRead),
      ).render(renderConfig)

      // nullsafe & typesafe
      val canonicalColumnRead =
        if (field.type.nullable) {
          "$recordId.${field.name.lowerSnake}?.let{ $serdeWrapped }"
        } else {
          serdeWrapped
        }

      output.append(indentation)
      output.append(indentation)
      output.append("${field.name.lowerCamel} = $canonicalColumnRead")
      output.append(",\n")
    }

    output.append(")")

    output.toString()
  }

  val modelToRecord: String by lazy {
    //TODO: move to expression class

    val indentation = "  "
    val entityId = "entity"
    val returnType = "${entity.name.upperCamel}Record"

    val argsSegment = listOf(
      "entity: ${entity.name.upperCamel}",
      if (entity.jvmView.requiresObjectWriter) "objectWriter: ObjectWriter" else "",
    )
      .filter { it.isNotBlank() }
      .joinToString(separator = ", ")

    // ------------------------------------------------
    val output = StringBuilder(1024)

    //TODO: [com.wcarmon.codegen.ast.MethodDeclarationExpression]
    output.append("fun toRecord($argsSegment): $returnType =\n")
    output.append("$indentation$returnType(\n")

    entity.sortedFieldsWithIdsFirst.map { field ->

      val read = WrapWithSerdeExpression(
        serde = effectiveJDBCSerde(field),
        serdeMode = SERIALIZE,
        wrapped = RawLiteralExpression("${entityId}.${field.name.lowerCamel}"),
      ).render(renderConfig)

      output.append(indentation)
      output.append(indentation)
      output.append("${field.name.lowerSnake} = $read")
      output.append(",\n")
    }

    output.append(")")

    output.toString()
  }

  private fun buildIndexConstraint(index: Index): String =
    index.fieldNames.joinToString(
      prefix = "UNIQUE(",
      separator = ", ",
      postfix = ")",
    ) {
      it.lowerSnake
    }
}
