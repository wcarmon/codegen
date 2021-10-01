package com.wcarmon.codegen.ast

import com.wcarmon.codegen.log.structuredWarn
import com.wcarmon.codegen.model.BaseFieldType.BOOLEAN
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage.SQL_DELIGHT
import com.wcarmon.codegen.util.effectiveSQLDelightTypeLiteral
import com.wcarmon.codegen.util.quoteTypeForSQLDelightLiteral
import org.apache.logging.log4j.LogManager

//TODO: document me
class SQLDelightColumnDeclarationExpression(
  private val field: Field,
) : Expression {

  companion object {
    @JvmStatic
    private val LOG = LogManager.getLogger(SQLDelightColumnDeclarationExpression::class.java)

    private const val NAME_WIDTH = 20
    private const val NULLABLE_WIDTH = 9
    private const val TYPE_WIDTH = 19
  }

  override val expressionName: String = SQLDelightColumnDeclarationExpression::class.java.simpleName

  /**
   * eg.  id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
   * eg.  name TEXT NOT NULL,
   * eg.  number INTEGER NOT NULL
   */
  override fun renderWithoutDebugComments(config: RenderConfig): String {

    val nameSegment = field.name.lowerSnake
    val nullableSegment = if (field.type.nullable) "" else "NOT NULL"

    val typeSegment = effectiveSQLDelightTypeLiteral(field)

    val defaultValueSegment = buildDefaultValueSegment(field)

    //TODO: handle autoIncrement

    return listOf(
      nameSegment.padEnd(NAME_WIDTH, ' '),
      typeSegment.padEnd(TYPE_WIDTH, ' '),
      nullableSegment.padEnd(NULLABLE_WIDTH, ' '),
      defaultValueSegment,
    ).joinToString(" ")
  }


  //TODO: revisit implementation after cleaning up default value handling
  @Deprecated("use DefaultValueExpression")
  private fun buildDefaultValueSegment(field: Field): String {

    val targetLanguage = SQL_DELIGHT
    var defaultValueLiteral: String? = null
    val effectiveBaseType = field.effectiveBaseType(targetLanguage)
    val effectiveDefaultValue = field.effectiveDefaultValue(targetLanguage)

    //TODO: move logic to DefaultValueExpression
    if (BOOLEAN == effectiveBaseType) {
      defaultValueLiteral = "0"
    }

    if (field.type.nullable) {
      defaultValueLiteral = "NULL"
    }

    if (effectiveDefaultValue.isPresent) {

      /* See [Field.defaultValueExpression] */
      //TODO: fix
      LOG.structuredWarn(
        "improve default value on sqlDelight column declaration",
        "effectiveDefaultValue" to effectiveDefaultValue,
        "field" to field,
        "targetLanguage" to targetLanguage,
      )

//      if (field.defaultValue.lowercase() != "null") {
//        //TODO: use field.defaultValue here
//        //TODO: use field.rdbmsConfig.overrideDefaultValue
//      }
    }

//    if (field.overrideDefaultValue(targetLanguage).isPresent) {
//      LOG.structuredWarn("Handle the rdbms override default here")
//    }

    if (defaultValueLiteral == null) {
      return ""
    }

    val q = quoteTypeForSQLDelightLiteral(field.effectiveBaseType(SQL_DELIGHT))

    val output = q.wrap(defaultValueLiteral)
    if (output.uppercase() == "\"NULL\"") {
      return "DEFAULT NULL"
    }

    return "DEFAULT $output"
  }
}
