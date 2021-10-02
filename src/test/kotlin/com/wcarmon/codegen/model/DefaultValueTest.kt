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

      val d = parsed.javaConfig.defaultValue
      check(
        testCase.expectedUninterpetedValue is String ==
            d.uninterpreted is String,
      ) {
        "Failed: wrong type on uninterpreted: $testCase" +
            "\ntestCase.expectedUninterpetedValue: " +
            (testCase.expectedUninterpetedValue?.javaClass?.name ?: "<null>") +
            "\nparsed.jvmConfig.defaultValue.uninterpreted: " +
            (d.uninterpreted?.javaClass?.name ?: "<null>")
      }

      // -- Assert
      assertEquals(
        testCase.expectedUninterpetedValue,
        d.uninterpreted
      ) {
        "Failed DefaultValue::uninterpreted: $testCase"
      }

      assertEquals(
        testCase.expectEmptyCollection,
        d.isEmptyCollection()
      ) {
        "Failed DefaultValue::isEmptyCollection: $testCase"
      }

      assertEquals(!testCase.expectPresent, d.isAbsent) {
        "Failed DefaultValue::isAbsent: $testCase"
      }

      assertEquals(testCase.expectPresent, d.isPresent) {
        "Failed DefaultValue::isPresent: $testCase"
      }

      assertEquals(testCase.expectShouldQuote, d.shouldQuote) {
        "Failed DefaultValue::shouldQuote: $testCase"
      }

      if (d.isPresent) {
        assertEquals(
          testCase.expectIsNullLiteral,
          d.isNullLiteral()
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
        | "java": ${testCase.jvmConfigJSON}
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
