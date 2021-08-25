package com.wcarmon.codegen.model.ast

import com.wcarmon.codegen.model.Documentation
import com.wcarmon.codegen.model.PackageName
import com.wcarmon.codegen.model.TargetLanguage

data class ClassDeclarationExpression(

  /** static or companion object */
  val classMethods: List<MethodDeclarationExpression> = listOf(),

  val documentation: Documentation = Documentation.EMPTY,

  val fields: List<FieldDeclarationExpression> = listOf(),

  val finalityModifier: FinalityModifier = FinalityModifier.FINAL,

  val fullyQualifiedInterfaces: List<String>,

  val instanceMethods: List<MethodDeclarationExpression> = listOf(),

  val name: Name,

  val packageName: PackageName,

  /**
   * Kotlin: data class
   * Java 14+: Record
   * Java 8+: Immutable pojo class
   */
  val pojo: Boolean = false,

  val validationExpressions: List<ValidationExpression>,

  val visibilityModifier: VisibilityModifier = VisibilityModifier.PUBLIC,

  //TODO: constructor

  //TODO: generic parameters
) : Expression {

  override fun serialize(targetLanguage: TargetLanguage, terminate: Boolean): String {
    TODO("Not yet implemented")
  }
}
