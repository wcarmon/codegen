package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.FinalityModifier.FINAL
import com.wcarmon.codegen.ast.VisibilityModifier.PRIVATE
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.util.javaTypeLiteral
import com.wcarmon.codegen.util.kotlinTypeLiteral

/**
 * Declares 1-field
 */
data class FieldDeclarationExpression(
  private val field: Field,

  private val annotations: Set<AnnotationExpression> = setOf(),
  private val defaultValue: Expression = EmptyExpression,
  private val documentation: DocumentationExpression = DocumentationExpression.EMPTY,
  private val finalityModifier: FinalityModifier = FINAL,
  private val visibilityModifier: VisibilityModifier = PRIVATE,
) : Expression {

  override val expressionName: String = FieldDeclarationExpression::class.java.simpleName

  override fun renderWithoutDebugComments(
    config: RenderConfig,
  ): String =
    when (config.targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(config)

      KOTLIN_JVM_1_4,
      -> handleKotlin(config.unterminated)

      else -> TODO("handle: $config")
    }

  private fun handleJava(
    config: RenderConfig,
  ): String {

    //TODO: support annotations

    val output = StringBuilder(512)

    val doc = documentation.render(config.unterminated)
    if (doc.isNotBlank()) {
      output.append("\n")
      output.append(doc)
      output.append("\n")
    }

    output.append(config.lineIndentation)

    val modifiers = buildJavaModifierFragments()
    if (modifiers.isNotEmpty()) {
      output.append(
        modifiers.joinToString(" ", postfix = " ")
      )
    }

    output.append(javaTypeLiteral(field, false))
    output.append(" ")
    output.append(field.name.lowerCamel)

    val dflt = defaultValue.render(config.unterminated)
    if (dflt.isNotBlank()) {
      output.append(" = $dflt")
    }

    if (config.terminate) {
      output.append(";")
    }

    return output.toString()
  }

  private fun handleKotlin(config: RenderConfig): String {

    val doc = documentation.render(config.unterminated)
    val dflt = defaultValue.render(config.unterminated)
    val typeLiteral = kotlinTypeLiteral(field, false)
    val variableKeyword = if (finalityModifier == FINAL) "val " else "var "

    val defaultValueFragment = if (dflt.isNotBlank()) " = $dflt" else ""
    val docFragment = if (doc.isNotBlank()) "\n$doc\n" else ""
    val visibilityFragment = visibilityModifier.render(config.targetLanguage)

    //TODO: support annotations
    return docFragment +
        config.lineIndentation +
        visibilityFragment +
        variableKeyword +
        field.name.lowerCamel +
        ":" +
        typeLiteral +
        defaultValueFragment
  }

  private fun buildJavaModifierFragments(): List<String> {
    val output = mutableListOf<String>()

    output += visibilityModifier.render(targetLanguage = JAVA_08)

    //TODO: "static" goes here (when required)

    if (finalityModifier == FINAL) {
      output += "final"
    }

    return output
  }
}
