package com.wcarmon.codegen.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.wcarmon.codegen.config.JSONBeans
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DefaultValueTest {

  lateinit var objectMapper: ObjectMapper

  data class D(
    val fieldJSON: String,

    val expectedHasValue: Boolean,
    val expectedDefaultValue: Any?,
  )

  // Assume: String
  // Non-null literals
  private val testCaseData = listOf(

    // Assume: no default
    D("""{  }""", false, "zzz"),
    D("""{ "defaultValue": null }""", false, "zzz"),
    D("""{ "defaultValue": {} }""", false, null),

    // Assume: null/nil/NULL
    D("""{ "defaultValue": {"value": null} }""", true, null),

    // Assume: non-null default value
    D("""{ "defaultValue": {"value": " "} }""", true, " "),
    D("""{ "defaultValue": {"value": ""} }""", true, ""),
    D("""{ "defaultValue": {"value": "''"} }""", true, "''"),
    D("""{ "defaultValue": {"value": "7"} }""", true, "7"),
    D("""{ "defaultValue": {"value": "\"\""} }""", true, "\"\""),
    D("""{ "defaultValue": {"value": "\"null\""} }""", true, "\"null\""),
    D("""{ "defaultValue": {"value": "false"} }""", true, "false"),
    D("""{ "defaultValue": {"value": "foo"} }""", true, "foo"),
    D("""{ "defaultValue": {"value": "nil"} }""", true, "nil"),
    D("""{ "defaultValue": {"value": "null"} }""", true, "null"),
    D("""{ "defaultValue": {"value": "NULL"} }""", true, "NULL"),
    D("""{ "defaultValue": {"value": "true"} }""", true, "true"),
    D("""{ "defaultValue": {"value": 3.1} }""", true, 3.1),
    D("""{ "defaultValue": {"value": 7} }""", true, 7),
    D("""{ "defaultValue": {"value": false} }""", true, false),
    D("""{ "defaultValue": {"value": true} }""", true, true),
  )

  @BeforeEach
  fun setUp() {
    objectMapper = JSONBeans().objectMapper()
  }

  @Test
  fun testHasValue() {
    testCaseData.forEachIndexed { index, testData ->

      val parsed = objectMapper.readValue(
        testData.fieldJSON,
        DefaultValue::class.java)

      assertEquals(testData.expectedHasValue, parsed.isPresent) {
        "failed defaultValue.hasValue check on $testData"
      }
    }
  }

  @Test
  fun testIsNull() {
    testCaseData.forEachIndexed { index, testData ->

      val parsed = objectMapper.readValue(
        testData.fieldJSON,
        DefaultValue::class.java)

      if (testData.expectedHasValue) {
        assertEquals(testData.expectedDefaultValue == null, parsed.isNullLiteral) {
          "failed: index=$index, testData=$testData"
        }

      } else {
        assertThrows<IllegalStateException>("failed: index=$index, testData=$testData") {
          parsed.isNullLiteral
        }
      }
    }
  }

  @Test
  fun testValueRead() {
    testCaseData.forEachIndexed { index, testData ->

      val parsed = objectMapper.readValue(
        testData.fieldJSON,
        DefaultValue::class.java)

      if (testData.expectedHasValue) {
        assertEquals(testData.expectedDefaultValue, parsed.literal) {
          "failed: index=$index, testData=$testData"
        }

      } else {
        assertThrows<IllegalStateException>("failed: index=$index, testData=$testData") {
          parsed.literal
        }
      }
    }
  }
}
