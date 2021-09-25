package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.wcarmon.codegen.config.JSONBeans
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DefaultValueTest {

  lateinit var objectMapper: ObjectMapper

  data class D(
    val fieldJSON: String,

    val expectedValuePresent: Boolean,
    val expectedDefaultValue: Any?,
  )

  // Assume: String
  // Non-null literals
  private val testCaseData = listOf(

    // Assume: no default
//    D("""{  }""", false, "zzz"),
//    D("""{ "defaultValue": null }""", false, "zzz"),
//    D("""{ "defaultValue": {} }""", false, null),

    // Assume Present, Assume: null/nil/NULL
    D("""{ "defaultValue": {"value": null} }""", true, null),

    // Assume Present: )non-null default value)
//    D("""{ "defaultValue": {"value": " "} }""", true, " "),
//    D("""{ "defaultValue": {"value": ""} }""", true, ""),
//    D("""{ "defaultValue": {"value": "''"} }""", true, "''"),
//    D("""{ "defaultValue": {"value": "7"} }""", true, "7"),
//    D("""{ "defaultValue": {"value": "\"\""} }""", true, "\"\""),
//    D("""{ "defaultValue": {"value": "\"null\""} }""", true, "\"null\""),
//    D("""{ "defaultValue": {"value": "false"} }""", true, "false"),
//    D("""{ "defaultValue": {"value": "foo"} }""", true, "foo"),
//    D("""{ "defaultValue": {"value": "nil"} }""", true, "nil"),
//    D("""{ "defaultValue": {"value": "null"} }""", true, "null"),
//    D("""{ "defaultValue": {"value": "NULL"} }""", true, "NULL"),
//    D("""{ "defaultValue": {"value": "true"} }""", true, "true"),
//    D("""{ "defaultValue": {"value": 3.1} }""", true, 3.1),
//    D("""{ "defaultValue": {"value": 7} }""", true, 7),
//    D("""{ "defaultValue": {"value": false} }""", true, false),
//    D("""{ "defaultValue": {"value": true} }""", true, true),
  )

  @BeforeEach
  fun setUp() {
    objectMapper = JSONBeans().objectMapper()
  }

  data class FakeField(
    @JsonProperty("defaultValue") val defaultValue: DefaultValue,
  )

  @Test
  fun testValuePresent() {
    testCaseData.forEachIndexed { index, testData ->

      val parsed = objectMapper
        .readerFor(FakeField::class.java)
        .readValue(
          testData.fieldJSON,
          FakeField::class.java)


      assertEquals(testData.expectedValuePresent, parsed.defaultValue.isPresent) {
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

      if (testData.expectedValuePresent) {
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

      if (testData.expectedValuePresent) {
        assertTrue(parsed.isPresent) {
          "Failed on parsed.isPresent: index=$index, testData=$testData"
        }

        assertEquals(testData.expectedDefaultValue, parsed.literal) {
          "Failed comparing default value: index=$index, testData=$testData"
        }

      } else {
        assertThrows<IllegalStateException>("failed: index=$index, testData=$testData") {
          parsed.literal
        }
      }
    }
  }
}
