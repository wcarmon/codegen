package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.buildResultSetGetterExpression
import com.wcarmon.codegen.util.getKotlinTypeLiteral

/**
 * Kotlin related convenience methods for a [Field]
 */
class KotlinFieldView(
  private val field: Field,
  private val jvmView: JVMFieldView,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isKotlin) {
      "invalid target language: $targetLanguage"
    }
  }

  val typeLiteral: String = getKotlinTypeLiteral(field.type)

  //TODO: test this on types that are already unqualified
  val unqualifiedType = getKotlinTypeLiteral(field.type, false)

  val resultSetGetterExpression by lazy {
    buildResultSetGetterExpression(field)
      .serialize(targetLanguage)
  }

  //TODO: more here
}
