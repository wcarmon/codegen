package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.wcarmon.codegen.config.JSONBeans
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal data class FakeField(
  @JsonProperty(
    value = "defaultValue",
    required = false)
  val defaultValue: DefaultValue? = DefaultValue(),
)

internal class DefaultValueTest {

  lateinit var objectMapper: ObjectMapper

  internal data class D(
    val fieldJSON: String,

    val expectPresent: Boolean,
    val expectedDefaultValue: Any?,
  )

  // Assume: String
  // Non-null literals
  private val testCaseData = listOf(

    // Assume: no default
    D("""{  }""", false, "zzz"),
    D("""{ "defaultValue": null }""", false, "zzz"),  //TODO: forbid since ambiguous
    D("""{ "defaultValue": {} }""", false, null),

    // Assume Present, Assume: null/nil/NULL
    D("""{ "defaultValue": {"value": null} }""", true, null),

    // Assume Present: )non-null default value)
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
  fun testValuePresent_whenPresent() {
    testCaseData
      .filter { it.expectPresent }
      .forEachIndexed { index, testData ->
        check(testData.expectPresent)

        val parsed = parseField(testData.fieldJSON)

        assertNotNull(parsed.defaultValue) {
          "failed default value must not be null check on $testData"
        }

        assertTrue(parsed.defaultValue!!.isPresent) {
          "failed defaultValue.isPresent check on $testData"
        }
      }
  }

  @Test
  fun testValuePresent_whenAbsent() {
    testCaseData
      .filter { !it.expectPresent }
      .forEachIndexed { index, testData ->
        check(!testData.expectPresent)

        val parsed = parseField(testData.fieldJSON)

        assertTrue(
          parsed.defaultValue == null ||
              !parsed.defaultValue.isPresent) {
          "failed defaultValue.isPresent check on $testData"
        }
      }
  }

  @Test
  fun testIsNull_whenPresent() {
    testCaseData
      .filter { it.expectPresent }
      .forEachIndexed { index, testData ->
        check(testData.expectPresent)

        val parsed = parseField(testData.fieldJSON)

        assertNotNull(parsed.defaultValue)

        assertEquals(testData.expectedDefaultValue == null,
          parsed.defaultValue!!.isNullLiteral) {
          "failed: index=$index, testData=$testData"
        }
      }
  }

  @Test
  fun testIsNull_whenAbsent() {
    testCaseData
      .filter { !it.expectPresent }
      .forEachIndexed { index, testData ->
        check(!testData.expectPresent)

        val parsed = parseField(testData.fieldJSON)

        if (parsed.defaultValue == null) {
          return
        }

        assertThrows<IllegalStateException>("failed: index=$index, testData=$testData") {
          parsed.defaultValue.isNullLiteral
        }
      }
  }

  @Test
  fun testValueRead_whenPresent() {
    testCaseData
      .filter { it.expectPresent }
      .forEachIndexed { index, testData ->
        check(testData.expectPresent)

        val parsed = parseField(testData.fieldJSON)

        assertNotNull(parsed.defaultValue)

        assertTrue(parsed.defaultValue!!.isPresent) {
          "Failed on parsed.isPresent: index=$index, testData=$testData"
        }

        assertEquals(testData.expectedDefaultValue, parsed.defaultValue.literal) {
          "Failed comparing default value: index=$index, testData=$testData"
        }
      }
  }

  @Test
  fun testValueRead_whenAbsent() {
    testCaseData
      .filter { !it.expectPresent }
      .forEachIndexed { index, testData ->
        check(!testData.expectPresent)

        val parsed = parseField(testData.fieldJSON)

        if (parsed.defaultValue == null) {
          return
        }

        assertThrows<IllegalStateException>("failed: index=$index, testData=$testData") {
          parsed.defaultValue.literal
        }
      }
  }


  private fun parseField(json: String) = objectMapper
    .readerFor(FakeField::class.java)
    .readValue(
      json,
      FakeField::class.java)
}
