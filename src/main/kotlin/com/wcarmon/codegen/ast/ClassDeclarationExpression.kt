package com.wcarmon.codegen.ast

import com.wcarmon.codegen.model.Name
import com.wcarmon.codegen.model.PackageName

data class ClassDeclarationExpression(

  /** static or companion object */
  private val classMethods: List<MethodDeclarationExpression> = listOf(),

  private val documentation: DocumentationExpression = DocumentationExpression.EMPTY,

  private val fields: List<FieldDeclarationExpression> = listOf(),

  private val finalityModifier: FinalityModifier = FinalityModifier.FINAL,

  private val fullyQualifiedInterfaces: List<String>,

  private val instanceMethods: List<MethodDeclarationExpression> = listOf(),

  private val name: Name,

  private val packageName: PackageName = PackageName.DEFAULT,

  /**
   * Kotlin: data class
   * Java 14+: Record
   * Java 8+: Immutable pojo class
   */
  private val pojo: Boolean = false,

  private val validationExpressions: List<FieldValidationExpressions> = listOf(),

  private val visibilityModifier: VisibilityModifier = VisibilityModifier.PUBLIC,

  //TODO: generic parameters
) : Expression {

  override val expressionName: String = ClassDeclarationExpression::class.java.simpleName

  override fun renderWithoutDebugComments(config: RenderConfig): String {
    TODO("Not yet implemented")
  }
}
