package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.defaultResultSetGetterMethod
import com.wcarmon.codegen.util.effectiveProtoSerde
import com.wcarmon.codegen.util.getKotlinTypeLiteral

/**
 * Kotlin related convenience methods for a [Field]
 */
class KotlinFieldView(
  private val field: Field,
  private val jvmView: JVMFieldView,
  private val rdbmsView: RDBMSColumnView,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isKotlin) {
      "invalid target language: $targetLanguage"
    }
  }

  val resultSetGetterExpression: String by lazy {
    ResultSetReadExpression(
      fieldName = field.name,
      getterMethod = defaultResultSetGetterMethod(field.effectiveBaseType),
      resultSetIdentifierExpression = RawExpression("rs"),
    )
      .render(targetLanguage, false)
  }

  val typeLiteral: String = getKotlinTypeLiteral(field.type, true)

  //TODO: test this on types that are already unqualified
  val unqualifiedType = getKotlinTypeLiteral(field.type, false)

  fun readFromProtoExpression(protoId: String = "proto") =
    ProtoFieldReadExpression.build(
      assertNonNull = false,  //TODO: verify
      field = field,
      fieldOwner = RawExpression(protoId),
    ).render(targetLanguage, false)


  fun writeToProtoExpression(pojoId: String = "entity"): String {

    val pojoReadExpression = FieldReadExpression(
      assertNonNull = false,
      fieldName = field.name,
      fieldOwner = RawExpression(pojoId),
      overrideFieldReadMode = DIRECT,
    )

    val serdeExpression = WrapWithSerdeExpression(
      serde = effectiveProtoSerde(field),
      serdeMode = SERIALIZE,
      wrapped = pojoReadExpression,
    )

    return ProtoFieldWriteExpression(
      field = field,
      sourceReadExpression = serdeExpression,
    )
      .render(targetLanguage, false)
  }

  fun updateFieldPreparedStatementSetterStatements(
    idFields: List<Field>,
  ) =
    rdbmsView.updateFieldPreparedStatementSetterStatements(
      idFields = idFields,
      targetLanguage = targetLanguage)
}
