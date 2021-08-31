package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.PackageName
import com.wcarmon.codegen.model.TargetLanguage.*

data class InterfaceDeclarationExpression(

  private val documentation: DocumentationExpression = DocumentationExpression.EMPTY,

  private val fullyQualifiedInterfaces: List<String>,

  private val instanceMethods: List<MethodHeaderExpression> = listOf(),

  private val defaultMethods: List<MethodDeclarationExpression> = listOf(),

  private val name: Name,

  private val packageName: PackageName,

  //TODO: List: generic parameters
) : Expression {

  override val expressionName = InterfaceDeclarationExpression::class.java.name

  val isFunctional by lazy {
    instanceMethods.size == 1
  }

  override fun render(
    config: RenderConfig,
  ): String = when (config.targetLanguage) {
    JAVA_08,
    JAVA_11,
    JAVA_17,
    -> handleJava(config)

    KOTLIN_JVM_1_4,
    -> handleKotlin(config)

    else -> TODO()
  }

  private fun handleJava(config: RenderConfig): String {
    TODO("Not yet implemented")
  }

  private fun handleKotlin(config: RenderConfig): String {
    TODO("Not yet implemented")
  }

}
