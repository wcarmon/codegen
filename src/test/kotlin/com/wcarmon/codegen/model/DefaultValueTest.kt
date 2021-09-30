package com.wcarmon.codegen.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.wcarmon.codegen.config.JSONBeans
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class DefaultValueTest {

  lateinit var objectMapper: ObjectMapper

  @BeforeEach
  fun setUp() {
    objectMapper = JSONBeans().objectMapper()
  }

  @Test
  fun testAllMethodsAndFields() {

    TEST_CASES_FOR_DEFAULT_VALUE.forEach { testCase ->

      // -- Act
      val parsed = parseField(testCase)

      check(
        testCase.expectedUninterpetedValue is String ==
            parsed.jvmConfig.defaultValue.uninterpreted is String,
      ) {
        "Failed: wrong type on uninterpreted: $testCase" +
            "\ntestCase.expectedUninterpetedValue: " +
            (testCase.expectedUninterpetedValue?.javaClass?.name ?: "<null>") +
            "\nparsed.jvmConfig.defaultValue.uninterpreted: " +
            (parsed.jvmConfig.defaultValue.uninterpreted?.javaClass?.name ?: "<null>")
      }

      // -- Assert
      assertEquals(
        testCase.expectedUninterpetedValue,
        parsed.jvmConfig.defaultValue.uninterpreted
      ) {
        "Failed DefaultValue::uninterpreted: $testCase"
      }

      assertEquals(
        testCase.expectEmptyCollection,
        parsed.jvmConfig.defaultValue.isEmptyCollection()
      ) {
        "Failed DefaultValue::isEmptyCollection: $testCase"
      }

      assertEquals(!testCase.expectPresent, parsed.jvmConfig.defaultValue.isAbsent) {
        "Failed DefaultValue::isAbsent: $testCase"
      }

      assertEquals(testCase.expectPresent, parsed.jvmConfig.defaultValue.isPresent) {
        "Failed DefaultValue::isPresent: $testCase"
      }

      assertEquals(testCase.expectShouldQuote, parsed.jvmConfig.defaultValue.shouldQuote) {
        "Failed DefaultValue::shouldQuote: $testCase"
      }

      if (parsed.jvmConfig.defaultValue.isPresent) {
        assertEquals(
          testCase.expectIsNullLiteral,
          parsed.jvmConfig.defaultValue.isNullLiteral()
        ) {
          "Failed DefaultValue::isNullLiteral: $testCase"
        }
      }
    }
  }

  private fun parseField(testCase: TC): Field {

    val fieldJSON = """
        |{
        | "name": "bar",
        | "type": "Foo",
        | "jvm": ${testCase.jvmConfigJSON}
        |}
       """.trimMargin()
      .replace("\n", "")

    return objectMapper
      .readerFor(Field::class.java)
      .readValue(
        fieldJSON,
        Field::class.java
      )
  }
}
