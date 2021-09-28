package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.wcarmon.codegen.config.JSONBeans
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal data class FakeField(
  @JsonProperty(
    value = "defaultValue",
    required = false
  )
  val defaultValue: DefaultValue? = DefaultValue(),
)

internal class DefaultValueTest {

  lateinit var objectMapper: ObjectMapper

  internal data class D(
    val fieldJSON: String,

    val expectPresent: Boolean,
    val expectedUninterpetedValue: Any?,
    val expectEmptyCollection: Boolean = false,
  )

  // Assume: String
  // Non-null literals
  private val testCaseData = listOf(

    // Assume Absent: no default
    D("""{  }""", false, "zzz"),
    D("""{ "defaultValue": null }""", false, "zzz"),  //TODO: forbid since ambiguous
    D("""{ "defaultValue": { {"quoteValue": false} }""", false, null),
    D("""{ "defaultValue": { {"quoteValue": true} }""", false, null),
    D("""{ "defaultValue": {} }""", false, null),

    // Assume Present, Assume: null/nil/NULL
    D("""{ "defaultValue": {"value": "null", "quoteValue": false} }""", true, null),
    D("""{ "defaultValue": {"value": null, "quoteValue": false} }""", true, null),
    D("""{ "defaultValue": {"value": null} }""", true, null),


    // Assume Present: (non-null default value)
    //TODO: add quoteValue permutations
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
    D("""{ "defaultValue": {"value": null, "quoteValue": true} }""", true, "null"),
    D("""{ "defaultValue": {"value": true} }""", true, true),
    D(
      """{ "defaultValue": {"value": [], "quoteValue": true} }""",
      expectPresent = true,
      expectedUninterpetedValue = "[]",
      expectEmptyCollection = false
    ),

    // Assume Present: empty collection
    D(
      """{ "defaultValue": {"value": []} }""",
      expectPresent = true,
      expectedUninterpetedValue = null,
      expectEmptyCollection = true
    ),
    D(
      """{ "defaultValue": {"value": [], "quoteValue": false} }""",
      expectPresent = true,
      expectedUninterpetedValue = null,
      expectEmptyCollection = true
    ),
  )

  @BeforeEach
  fun setUp() {
    objectMapper = JSONBeans().objectMapper()
  }

  @Test
  fun testIsPresent_whenPresent() {
    testCaseData
      .filter { it.expectPresent }
      .forEach { testData ->

        // -- Act
        val parsed = parseField(testData.fieldJSON)

        checkNotNull(parsed.defaultValue) {
          "default value must be non-null: $testData"
        }

        // -- Assert
        assertTrue(parsed.defaultValue.isPresent) {
          "defaultValue must show present: $testData"
        }

        assertFalse(parsed.defaultValue.isAbsent) {
          "defaultValue must show non-absent: $testData"
        }
      }
  }

  @Test
  fun testIsPresent_whenAbsent() {
    testCaseData
      .filter { !it.expectPresent }
      .forEach { testData ->

        // -- Act
        val parsed = parseField(testData.fieldJSON)

        if (parsed.defaultValue == null) {
          // pass
          return
        }

        // -- Assert
        assertTrue(parsed.defaultValue.isAbsent) {
          "defaultValue must be absent: $testData"
        }

        assertFalse(parsed.defaultValue.isPresent) {
          "defaultValue must be non-present: $testData"
        }
      }
  }

  @Test
  fun testIsNullLiteral_whenPresent() {
    testCaseData
      .filter { it.expectPresent }
      .forEach { testData ->

        // -- Act
        val parsed = parseField(testData.fieldJSON)

        checkNotNull(parsed.defaultValue) {
          "Field.defaultValue must be non-null: $testData"
        }

        // -- Assert
        if (testData.expectEmptyCollection) {
          assertFalse(parsed.defaultValue.isNullLiteral()) {
            // empty collection is not the same as null literal
            "failed: testData=$testData"
          }

        } else {
          // non-collection
          //TODO: simplify me
          assertEquals(
            testData.expectedUninterpetedValue == null,
            parsed.defaultValue.isNullLiteral()
          ) {
            "failed: testData=$testData"
          }
        }
      }
  }


  @Test
  fun testIsNullLiteral_whenAbsent() {
    testCaseData
      .filterNot { it.expectPresent }
      .forEach { testData ->

        // -- Act
        val parsed = parseField(testData.fieldJSON)

        if (parsed.defaultValue == null) {
          // pass
          return
        }

        // -- Assert
        assertFalse(parsed.defaultValue.isNullLiteral()) {
          "absent is different from null literal: $testData"
        }
      }
  }

  @Test
  fun testValueRead_whenPresent() {
    testCaseData
      .filter { it.expectPresent }
      .forEach { testData ->

        // -- Act
        val parsed = parseField(testData.fieldJSON)

        checkNotNull(parsed.defaultValue)

        check(parsed.defaultValue.isPresent) {
          "Failed on parsed.isPresent: testData=$testData"
        }

        // -- Assert
        assertEquals(
          testData.expectedUninterpetedValue,
          parsed.defaultValue.uninterpreted
        ) {
          "Unexpected uninterpreted value: testData=$testData"
        }
      }
  }

  @Test
  fun testValueRead_whenAbsent() {
    testCaseData
      .filterNot { it.expectPresent }
      .forEach { testData ->

        // -- Act
        val parsed = parseField(testData.fieldJSON)

        if (parsed.defaultValue == null) {
          // pass
          return
        }

        // -- Assert
        assertNull(parsed.defaultValue.uninterpreted)
      }
  }


  @Test
  fun testIsEmptyCollection_whenCollection() {

    testCaseData
      .filter {
        it.expectEmptyCollection
      }
      .forEach { testData ->

        // -- Act
        val parsed = parseField(testData.fieldJSON)

        checkNotNull(parsed.defaultValue)


        // -- Assert
        assertFalse(parsed.defaultValue.isAbsent) { "Failed: $testData" }
        assertFalse(parsed.defaultValue.isNullLiteral()) { "Failed: $testData" }
        assertNull(parsed.defaultValue.uninterpreted) { "Failed: $testData" }
        assertTrue(parsed.defaultValue.isEmptyCollection()) { "Failed: $testData" }
        assertTrue(parsed.defaultValue.isPresent) { "Failed: $testData" }
      }
  }

  @Test
  fun testIsEmptyCollection_whenNonCollection() {
    testCaseData
      .filter {
        it.expectEmptyCollection
      }
      .forEach { testData ->

        // -- Act
        val parsed = parseField(testData.fieldJSON)

        checkNotNull(parsed.defaultValue)

        // -- Assert
        TODO()
      }
  }

  private fun parseField(json: String) = objectMapper
    .readerFor(FakeField::class.java)
    .readValue(
      json,
      FakeField::class.java
    )
}

// TODO: test uninterpreted
// TODO: test shouldQuote
