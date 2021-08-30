package com.wcarmon.codegen.ast

import com.wcarmon.codegen.ast.FinalityModifier.FINAL
import com.wcarmon.codegen.ast.VisibilityModifier.PRIVATE
import com.wcarmon.codegen.model.LogicalFieldType
import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.*
import com.wcarmon.codegen.util.getKotlinTypeLiteral
import com.wcarmon.codegen.util.javaTypeLiteral

/**
 * Declares 1-field
 */
data class FieldDeclarationExpression(
  private val name: Name,
  private val type: LogicalFieldType,

  private val defaultValue: Expression = EmptyExpression,
  private val documentation: DocumentationExpression = DocumentationExpression.EMPTY,
  private val finalityModifier: FinalityModifier = FINAL,
  private val visibilityModifier: VisibilityModifier = PRIVATE,
) : Expression {

  override fun render(
    targetLanguage: TargetLanguage,
    terminate: Boolean,
    lineIndentation: String,
  ): String =
    when (targetLanguage) {
      JAVA_08,
      JAVA_11,
      JAVA_17,
      -> handleJava(
        targetLanguage,
        terminate,
        lineIndentation)

      KOTLIN_JVM_1_4,
      -> handleKotlin(
        targetLanguage,
        lineIndentation)

      else -> TODO("handle: $targetLanguage")
    }

  private fun handleJava(
    targetLanguage: TargetLanguage,
    terminate: Boolean = true,
    lineIndentation: String,
  ): String {


    val output = StringBuilder(512)

    val doc = documentation.render(targetLanguage, false, lineIndentation)
    if (doc.isNotBlank()) {
      output.append(doc)
      output.append("\n")
    }

    output.append(lineIndentation)

    val modifiers = buildJavaModifierFragments()
    if (modifiers.isNotEmpty()) {
      output.append(
        modifiers.joinToString(" ", postfix = " "))
    }

    output.append(javaTypeLiteral(type, false))
    output.append(" ")
    output.append(name.lowerCamel)

    val dflt = defaultValue.render(targetLanguage)
    if (dflt.isNotBlank()) {
      TODO("handle default value: $dflt")
    }

    if (terminate) {
      output.append(";")
    }

    return output.toString()
  }

  private fun handleKotlin(
    targetLanguage: TargetLanguage,
    lineIndentation: String,
  ): String {

    val doc = documentation.render(targetLanguage, false, lineIndentation)
    val dValue = defaultValue.render(targetLanguage, false)
    val typeLiteral = getKotlinTypeLiteral(type, false)
    val variableKeyword = if (finalityModifier == FINAL) "val " else "var "

    val defaultValueFragment = if (dValue.isNotBlank()) " = $dValue" else ""
    val docFragment = if (doc.isNotBlank()) doc + "\n" else ""
    val visibilityFragment = visibilityModifier.render(targetLanguage)

    return docFragment +
        lineIndentation +
        visibilityFragment +
        variableKeyword +
        name.lowerCamel +
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
