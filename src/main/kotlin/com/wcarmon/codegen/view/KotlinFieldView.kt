package com.wcarmon.codegen.view

import com.wcarmon.codegen.ast.*
import com.wcarmon.codegen.ast.FieldReadMode.DIRECT
import com.wcarmon.codegen.ast.FinalityModifier.FINAL
import com.wcarmon.codegen.ast.VisibilityModifier.PUBLIC
import com.wcarmon.codegen.model.BaseFieldType
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.FieldValidation
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

  fun fakeFieldAssignment(): String {
    //TODO: use constraints (for strings & numbers)
    //TODO: handle user defined types (json config)

    val testConfig = field.effectiveTestConfig(targetLanguage)
    val validation = field.effectiveFieldValidation(targetLanguage)

    val nonNullRightSide =
      if (testConfig.randomValueBuilder.isNotBlank()) {
        testConfig.randomValueBuilder

      } else if (field.type.enumType) {
        //TODO: for java, use .length (instead of .size)
        "${field.type.rawTypeLiteral}.values()[" +
            "ThreadLocalRandom.current().nextInt(${field.type.rawTypeLiteral}.values().size)" +
            "]"

      } else {
        fakerExpression(validation, field.type.base)
      }

    //TODO: randomly null for nullable  <- if (ThreadLocalRandom.current().nextBoolean()) null else TODO()

    return "${field.name.lowerCamel} = $nonNullRightSide"
  }


  //TODO: extract so other JVM languages can use
  //TODO: handle when there's a regex, see faker.regexify()
  private fun fakerExpression(
    validation: FieldValidation,
    baseType: BaseFieldType
  ) = when (baseType) {
//    BOOLEAN -> "faker.bool().bool()"
    BOOLEAN -> "ThreadLocalRandom.current().nextBoolean()"
    COLOR -> "faker.color().hex(true)"
    DURATION -> "Duration.ofSeconds(faker.number().numberBetween(10, 86_400).toLong())"
    INT_16 -> {
      val low = validation.minValue?.toString() ?: "Short.MIN_VALUE"
      val high = validation.maxValue?.toString() ?: "Short.MAX_VALUE"
      "faker.number().numberBetween($low, $high)"
    }
    INT_32 -> {
      val low = validation.minValue?.toString() ?: "Int.MIN_VALUE"
      val high = validation.maxValue?.toString() ?: "Int.MAX_VALUE"
//      "faker.number().numberBetween($low, $high)"
      "ThreadLocalRandom.current().nextInt($low, $high)"
    }
    INT_64 -> {
      val low = validation.minValue?.toString() ?: "Long.MIN_VALUE"
      val high = validation.maxValue?.toString() ?: "Long.MAX_VALUE"
      "ThreadLocalRandom.current().nextLong($low, $high)"
    }
//    EMAIL -> "faker.bothify(\"???##@gmail.com\")"
    EMAIL -> "faker.internet().safeEmailAddress()"

    //TODO: Set
    //TODO: bigInt
    //TODO: bigDecimal

    UTC_INSTANT -> "faker.date().past(365, TimeUnit.DAYS).toInstant()"

    FLOAT_32 -> {
      val low = validation.minValue?.toString() ?: "Float.MIN_VALUE"
      val high = validation.maxValue?.toString() ?: "Float.MAX_VALUE"
      "faker.number().randomDouble(1, $low, $high) as Float"
    }
    FLOAT_64 -> {
      val low = validation.minValue?.toString() ?: "Double.MIN_VALUE"
      val high = validation.maxValue?.toString() ?: "Double.MAX_VALUE"
      "faker.number().randomDouble(2, $low, $high)"
    }
    PERIOD -> "Period.ofDays(ThreadLocalRandom.current().nextInt(60))"
    STRING -> {
      val maxLength = validation.maxSize ?: 32
      //GOTCHA: substring fails if upper limit is beyond string length
      "faker.lorem().sentence(20).substring(0, $maxLength).trim()"
    }
    URI -> {
      //TODO: allow file:// uris  <-- faker.file().fileName()
      "URI(faker.internet().url())"
    }
    URL -> "faker.internet().url()"
    UUID -> "UUID.fromString(faker.internet().uuid())"

    // faker.address().cityName().replace(' ', '_').lowercase()

    else -> {
      System.err.println("TODO: handle: ${field.type.base} (${field.name.lowerCamel})")
      "\"fix-$baseType\""
    }
  }
}
