package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.DefaultValue
import com.wcarmon.codegen.model.TargetLanguage.*

data class DefaultValueExpression(
  //TODO: might need field type information
  private val defaultValue: DefaultValue = DefaultValue(),
) : Expression {

  override val expressionName: String = DefaultValueExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig): String {

    if (defaultValue.isAbsent) {
      return EmptyExpression.render(config)
    }

    if (defaultValue.isNullLiteral) {
      return NullLiteralExpression.render(config)
    }

    return when (config.targetLanguage) {

      GOLANG_1_7,
      -> handleGolang(config)

      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4,
      -> handleKotlin(config)

      else -> TODO("finish rendering defaultValue Expression for $config")
    }
  }

  private fun handleGolang(config: RenderConfig): String {
    TODO("Not yet implemented")
  }

  private fun handleJava(config: RenderConfig): String {
    TODO("Not yet implemented")
  }

  private fun handleKotlin(config: RenderConfig): String {
    //TODO: quote properly based on language and type
    //      return quoteTypeForJVMLiterals(field.type.base)
    //        .wrap(field.defaultValue.literal.toString())

//    RawLiteralExpression("field.defaultValue.value")

    TODO("Not yet implemented")
  }
}
