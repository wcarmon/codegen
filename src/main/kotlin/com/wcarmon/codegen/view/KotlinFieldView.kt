package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.ast.FinalityModifier.FINAL
import com.wcarmon.codegen.ast.VisibilityModifier.PUBLIC
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.TargetLanguage.SQL_POSTGRESQL
import com.wcarmon.codegen.util.defaultResultSetGetterMethod
import com.wcarmon.codegen.util.kotlinTypeLiteral

/**
 * Kotlin related convenience methods for a [Field]
 */
class KotlinFieldView(
  debugMode: Boolean,
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

  val defaultValueLiteral: String by lazy {
    DefaultValueExpression(field)
      .render(renderConfig)
  }

  val fieldDeclaration: String by lazy {
    FieldDeclarationExpression(
      defaultValue = DefaultValueExpression(field),
      documentation = DocumentationExpression(field.documentation),
      field = field,
      finalityModifier = FINAL,
      visibilityModifier = PUBLIC,
      //TODO: annotations
    ).render(renderConfig)
  }

  val isCollection: Boolean by lazy {
    field.effectiveBaseType(targetLanguage).isCollection
  }

  val resultSetGetterExpression: String by lazy {

    val wrapped =
      ResultSetReadExpression(
        fieldName = field.name,
        getterMethod = defaultResultSetGetterMethod(field.effectiveBaseType(SQL_POSTGRESQL)),
        resultSetIdentifierExpression = RawLiteralExpression("rs"),
      )

    WrapWithSerdeExpression(
      serde = field.effectiveRDBMSSerde(targetLanguage),
      serdeMode = DESERIALIZE,
      wrapped = wrapped,
    )
      .render(renderConfig.unindented)
  }

  val typeLiteral: String = field.effectiveTypeLiteral(targetLanguage, true)

  //TODO: test this on types that are already unqualified
  val unqualifiedType = kotlinTypeLiteral(field, false)

  val typeParameters: List<String> =
    field.typeParameters(targetLanguage)

  fun readFromProtoExpression(protobufId: String = "proto") =
    ProtobufFieldReadExpression(
      assertNonNull = false,
      field = field,
      fieldOwner = RawLiteralExpression(protobufId),
      serde = field.effectiveProtobufSerde(targetLanguage),
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
      serde = field.effectiveProtobufSerde(targetLanguage),
      serdeMode = SERIALIZE,
      wrapped = pojoReadExpression,
    )

    return ProtobufFieldWriteExpression(
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

    val serdes = field.effectiveProtobufSerdesForTypeParameters(targetLanguage)
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

    val serdes = field.effectiveProtobufSerdesForTypeParameters(targetLanguage)
    check(serdes.size > typeParameterNumber) {
      "serde count: ${serdes.size}, requested index: $typeParameterNumber"
    }

    return serdes[typeParameterNumber]
      .forMode(SERIALIZE)
      .expand(thingToSerialize)
  }

}
