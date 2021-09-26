package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.ProtobufFieldNumber
import com.wcarmon.codegen.model.TargetLanguage.PROTO_BUF_3

//TODO: document me
data class ProtobufFieldDeclarationExpression(
  private val field: Field,
  val number: ProtobufFieldNumber,

  private val deprecated: Boolean = false,
  private val repeated: Boolean = false,

  //TODO: DocumentationExpression
) : Expression, Comparable<ProtobufFieldDeclarationExpression> {

  override val expressionName: String = ProtobufFieldDeclarationExpression::class.java.simpleName

  companion object {

    @JvmStatic
    val NUMBER_COMPARATOR = Comparator<ProtobufFieldDeclarationExpression> { x, y ->
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
        field.effectiveTypeLiteral(PROTO_BUF_3) +
        " " +
        field.name.lowerSnake +
        " = " +
        number.value +
        deprecatedSegment +
        ";"
  }

  override fun compareTo(other: ProtobufFieldDeclarationExpression) =
    number.compareTo(other.number)
}
