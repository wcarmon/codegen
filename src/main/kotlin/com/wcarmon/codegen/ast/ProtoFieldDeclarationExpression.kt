package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.ProtoFieldNumber
import com.wcarmon.codegen.util.effectiveProtobufType

//TODO: document me
data class ProtoFieldDeclarationExpression(
  private val field: Field,
  val number: ProtoFieldNumber,


  private val deprecated: Boolean = false,
  private val repeated: Boolean = false,

  //TODO: DocumentationExpression
) : Expression, Comparable<ProtoFieldDeclarationExpression> {

  override val expressionName: String = ProtoFieldDeclarationExpression::class.java.simpleName

  companion object {

    @JvmStatic
    val NUMBER_COMPARATOR = Comparator<ProtoFieldDeclarationExpression> { x, y ->
      x.number.compareTo(y.number)
    }
  }

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ): String {
    check(config.targetLanguage.isProtobuf)
    check(config.terminate)

    val repeatedPrefix =
      if (repeated) "repeated "
      else ""

    val deprecatedSegment =
      if (deprecated) " [deprecated = true]"
      else ""

    return config.lineIndentation +
        repeatedPrefix +
        effectiveProtobufType(field) +
        " " +
        field.name.lowerSnake +
        " = " +
        number.value +
        deprecatedSegment +
        ";"
  }

  override fun compareTo(other: ProtoFieldDeclarationExpression) =
    number.compareTo(other.number)
}
