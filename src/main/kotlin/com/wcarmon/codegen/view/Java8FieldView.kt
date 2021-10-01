package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.ast.FieldReadMode.GETTER
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.DESERIALIZE
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.defaultResultSetGetterMethod
import com.wcarmon.codegen.util.javaTypeLiteral
import com.wcarmon.codegen.util.newJavaCollectionExpression
import com.wcarmon.codegen.util.unmodifiableJavaCollectionMethod

/**
 * Java related convenience methods for a [Field]
 *
 * Pre-rendered [Expression]s
 */
class Java8FieldView(
  debugMode: Boolean,
  private val field: Field,
  private val jvmView: JVMFieldView,
  private val rdbmsView: RDBMSColumnView,
  private val targetLanguage: TargetLanguage,
) {

  init {
    require(targetLanguage.isJava) {
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

  val isCollection: Boolean by lazy {
    field.effectiveBaseType(targetLanguage).isCollection
  }

  val resultSetGetterExpression: String by lazy {

    val wrapped =
      ResultSetReadExpression(
        fieldName = field.name,
        getterMethod = defaultResultSetGetterMethod(field.effectiveBaseType(targetLanguage)),
        resultSetIdentifierExpression = RawLiteralExpression("rs"),
      )

    WrapWithSerdeExpression(
      serde = field.effectiveRDBMSSerde(targetLanguage),
      serdeMode = DESERIALIZE,
      wrapped = wrapped,
    )
      .render(renderConfig.unterminated.unindented)
  }

  val typeLiteral: String = field.effectiveTypeLiteral(targetLanguage, true)

  //TODO: test this on types that are already unqualified
  val unqualifiedType: String = javaTypeLiteral(field, false)

  //TODO: rename me
  val protobufSerializeExpressionForTypeParameters: String by lazy {
    TODO("fix")
//    protoReadExpressionForTypeParameters(
//      field,
//      listOf(RawExpression("item")),
//      SERIALIZE)
//      .map {
//        it.serialize(targetLanguage)
//      }
  }

  //TODO: convert to fun,
  //    accept template placeholder replacement here
  //    rename
  val unmodifiableCollectionMethod: String by lazy {
    unmodifiableJavaCollectionMethod(field.effectiveBaseType(targetLanguage))
  }

  fun equalityExpression(
    thisId: String,
    thatId: String,
  ) = equalityExpression(
    RawLiteralExpression(thisId),
    RawLiteralExpression(thatId)
  )

  private fun equalityExpression(
    expression0: Expression = RawLiteralExpression("this"),
    expression1: Expression = RawLiteralExpression("that"),
  ): String = EqualityTestExpression(
    expression0 = expression0,
    expression1 = expression1,
    expressionType = field.type,
  )
    .render(renderConfig.unterminated)

  val fieldDeclarationForBuilder: String by lazy {
    FieldDeclarationExpression(
      //TODO: suffix "ID/Primary key" when `field.idField`
      defaultValue = DefaultValueExpression(field),
      documentation = DocumentationExpression.EMPTY,
      field = field,
      finalityModifier = FinalityModifier.NON_FINAL,
      visibilityModifier = VisibilityModifier.PRIVATE,
      //TODO: annotations
    ).render(renderConfig.indented.terminated)

  }

  val fieldDeclaration: String by lazy {
    FieldDeclarationExpression(
      //TODO: suffix "ID/Primary key" when `field.idField`
      defaultValue = EmptyExpression,
      documentation = DocumentationExpression(field.documentation),
      field = field,
      finalityModifier = FinalityModifier.FINAL,
      visibilityModifier = VisibilityModifier.PRIVATE,
      //TODO: annotations
    ).render(renderConfig.indented.terminated)
  }

  val typeParameters: List<String> =
    field.typeParameters(targetLanguage)

  fun readFromProtobufExpression(protobufId: String = "proto") =
    ProtobufFieldReadExpression(
      assertNonNull = false,
      field = field,
      fieldOwner = RawLiteralExpression(protobufId),
      serde = field.effectiveProtobufSerde(targetLanguage),
    )
      .render(renderConfig.unterminated)

  fun writeToProtobufExpression(pojoId: String = "entity"): String {

    val pojoReadExpression = FieldReadExpression(
      assertNonNull = false,
      fieldName = field.name,
      fieldOwner = RawLiteralExpression(pojoId),
      overrideFieldReadMode = GETTER,
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
      .render(renderConfig.unterminated)
  }

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


  fun updateFieldPreparedStatementSetterStatements(
    idFields: List<Field>,
    fieldForUpdateTimestamp: Field?,
  ): String =
    rdbmsView.updateFieldPreparedStatementSetterStatements(
      fieldForUpdateTimestamp = fieldForUpdateTimestamp,
      idFields = idFields,
      targetLanguage = targetLanguage,
    )

  // GOTCHA: Only invoke on collection types
  fun newCollectionExpression() = newJavaCollectionExpression(field.type)
}


