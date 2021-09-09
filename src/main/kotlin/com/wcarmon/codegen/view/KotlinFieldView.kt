package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.*

/**
 * Kotlin related convenience methods for a [Field]
 */
class KotlinFieldView(
  private val debugMode: Boolean,
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

  private val renderConfig = RenderConfig(
    debugMode = debugMode,
    targetLanguage = targetLanguage,
    terminate = false
  )

  val resultSetGetterExpression: String by lazy {

    val wrapped =
      ResultSetReadExpression(
        fieldName = field.name,
        getterMethod = defaultResultSetGetterMethod(field.effectiveBaseType),
        resultSetIdentifierExpression = RawLiteralExpression("rs"),
      )

    WrapWithSerdeExpression(
      serde = effectiveJDBCSerde(field),
      serdeMode = DESERIALIZE,
      wrapped = wrapped,
    )
      .render(renderConfig.unindented)
  }

  val typeLiteral: String = getKotlinTypeLiteral(field.type, true)

  //TODO: test this on types that are already unqualified
  val unqualifiedType = getKotlinTypeLiteral(field.type, false)

  fun readFromProtoExpression(protoId: String = "proto") =
    ProtoFieldReadExpression(
      assertNonNull = false,
      field = field,
      fieldOwner = RawLiteralExpression(protoId),
    )
      .render(renderConfig)


  fun writeToProtoExpression(pojoId: String = "entity"): String {

    val pojoReadExpression = FieldReadExpression(
      assertNonNull = false,
      fieldName = field.name,
      fieldOwner = RawLiteralExpression(pojoId),
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
      .render(renderConfig)
  }

  fun updateFieldPreparedStatementSetterStatements(
    idFields: List<Field>,
    fieldForUpdateTimestamp: Field?,
  ) =
    rdbmsView.updateFieldPreparedStatementSetterStatements(
      fieldForUpdateTimestamp = fieldForUpdateTimestamp,
      idFields = idFields,
      targetLanguage = targetLanguage,
    )

  fun deserializerForTypeParameter(
    typeParameterNumber: Int = 0,
    thingToDeserialize: String,
  ): String {
    require(typeParameterNumber >= 0)

    val serdes = effectiveProtoSerdesForTypeParameters(field)
    check(serdes.size > typeParameterNumber) {
      "serde count: ${serdes.size}, requested index: $typeParameterNumber"
    }

    return serdes[typeParameterNumber]
      .forMode(DESERIALIZE)
      .expand(thingToDeserialize)
  }

  fun serializerForTypeParameter(
    typeParameterNumber: Int = 0,
    thingToSerialize: String,
  ): String {
    require(typeParameterNumber >= 0)

    val serdes = effectiveProtoSerdesForTypeParameters(field)
    check(serdes.size > typeParameterNumber) {
      "serde count: ${serdes.size}, requested index: $typeParameterNumber"
    }

    return serdes[typeParameterNumber]
      .forMode(SERIALIZE)
      .expand(thingToSerialize)
  }

}
