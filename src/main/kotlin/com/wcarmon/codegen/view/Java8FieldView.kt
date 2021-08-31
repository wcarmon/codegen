package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.ast.FieldReadMode.GETTER
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.SerdeMode.SERIALIZE
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.util.defaultResultSetGetterMethod
import com.wcarmon.codegen.util.effectiveProtoSerde
import com.wcarmon.codegen.util.javaTypeLiteral
import com.wcarmon.codegen.util.newJavaCollectionExpression

/**
 * Java related convenience methods for a [Field]
 *
 * Pre-rendered [Expression]s
 */
class Java8FieldView(
  private val debugMode: Boolean,
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

  val resultSetGetterExpression: String by lazy {

    ResultSetReadExpression(
      fieldName = field.name,
      getterMethod = defaultResultSetGetterMethod(field.effectiveBaseType),
      resultSetIdentifierExpression = RawExpression("rs"),
    )
      .render(renderConfig.terminated.indented)
  }

  val typeLiteral: String = javaTypeLiteral(field.type, true)

  //TODO: test this on types that are already unqualified
  val unqualifiedType: String = javaTypeLiteral(field.type, false)

  //TODO: rename me
  val protoSerializeExpressionForTypeParameters: String by lazy {
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
    TODO("fix")
//    unmodifiableJavaCollectionMethod(field.effectiveBaseType)
  }

  fun equalityExpression(
    thisId: String,
    thatId: String,
  ) = equalityExpression(
    RawExpression(thisId),
    RawExpression(thatId))

  fun equalityExpression(
    expression0: Expression = RawExpression("this"),
    expression1: Expression = RawExpression("that"),
  ): String = EqualityTestExpression(
    expression0 = expression0,
    expression1 = expression1,
    expressionType = field.type,
  ).render(renderConfig.unterminated)

  //TODO: improve this
//  val protoDeserializeExpressionForTypeParameters by lazy {
//    protoReadExpressionForTypeParameters(
//      field,
//      listOf(RawExpression("item")),
//      DESERIALIZE)
//      .map {
//        it.serialize(targetLanguage)
//      }
//  }


  fun readFromProtoExpression(protoId: String = "proto") =
    ProtoFieldReadExpression.build(
      assertNonNull = false,
      field = field,
      fieldOwner = RawExpression(protoId),
    ).render(renderConfig.unterminated)

  fun writeToProtoExpression(pojoId: String = "entity"): String {

    val pojoReadExpression = FieldReadExpression(
      assertNonNull = false,
      fieldName = field.name,
      fieldOwner = RawExpression(pojoId),
      overrideFieldReadMode = GETTER,
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
      .render(renderConfig.unterminated)
  }

  fun updateFieldPreparedStatementSetterStatements(
    idFields: List<Field>,
  ): String =
    rdbmsView.updateFieldPreparedStatementSetterStatements(
      idFields = idFields,
      targetLanguage = targetLanguage,
    )


  // GOTCHA: Only invoke on collection types
  fun newCollectionExpression() = newJavaCollectionExpression(field.type)
}


