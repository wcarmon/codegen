package com.wcarmon.codegen.model

internal data class TC(

  val jvmConfigJSON: String,

  // -- one for each exposed method/property
  val expectedUninterpetedValue: Any?,
  val expectEmptyCollection: Boolean,
  val expectIsNullLiteral: Boolean,
  val expectPresent: Boolean,
  val expectShouldQuote: Boolean,
)


internal val TEST_CASES_FOR_DEFAULT_VALUE = listOf(

//  // ---------------------------------------------------
//  // -- Assume Absent, no default
//  TC(
//    jvmConfigJSON = """{ }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = false,
//    expectPresent = false,
//  expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{"defaultValue": null}""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = false,
//    expectPresent = false,
  //  expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"quoteValue": false} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = false,
//    expectPresent = false,
//    expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"quoteValue": true} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = false,
//    expectPresent = false,
//    expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = false,
//    expectPresent = false,
//    expectShouldQuote = false,
//  ),
//
//
//  // ---------------------------------------------------
//  // -- Assume Present, Assume: null/nil/NULL literal
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": "null", "quoteValue": false} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = true,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": null, "quoteValue": false} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = true,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": null} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = true,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "nil", "quoteValue": false} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = true,
    expectPresent = true,
    expectShouldQuote = false,
  ),

//  // ---------------------------------------------------
//  // -- Assume Present: empty collection
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": []} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = true,
//    expectIsNullLiteral = false,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": [], "quoteValue": false} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = true,
//    expectIsNullLiteral = false,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": {}} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = true,
//    expectIsNullLiteral = false,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": {}, "quoteValue": false} }""",
//    expectedUninterpetedValue = null,
//    expectEmptyCollection = true,
//    expectIsNullLiteral = false,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),


  // ---------------------------------------------------
  // -- Assume Present: non-null, non-empty-collection
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": [], "quoteValue": true} }""",
    expectedUninterpetedValue = "[]",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "null", "quoteValue": true}}""",
    expectedUninterpetedValue = "null",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": " "} }""",
    expectedUninterpetedValue = " ",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": " ", "quoteValue": true} }""",
    expectedUninterpetedValue = " ",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": " ", "quoteValue": false} }""",
    expectedUninterpetedValue = " ",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": ""} }""",
    expectedUninterpetedValue = "",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "", "quoteValue": false} }""",
    expectedUninterpetedValue = "",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "", "quoteValue": true} }""",
    expectedUninterpetedValue = "",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "''"} }""",
    expectedUninterpetedValue = "''",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "''", "quoteValue": true} }""",
    expectedUninterpetedValue = "''",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "''", "quoteValue": false} }""",
    expectedUninterpetedValue = "''",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "nil", "quoteValue": true}}""",
    expectedUninterpetedValue = "nil",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "\"\"", "quoteValue": true} }""",
    expectedUninterpetedValue = "\"\"",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "\"\"", "quoteValue": false} }""",
    expectedUninterpetedValue = "\"\"",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),

  //TODO: add quoteValue permutations
//    D("""{ "defaultValue": {"value": "7"} }""", true, "7"),
//    D("""{ "defaultValue": {"value": "\"\""} }""", true, "\"\""),
//    D("""{ "defaultValue": {"value": "\"null\""} }""", true, "\"null\""),
//    D("""{ "defaultValue": {"value": "false"} }""", true, "false"),
//    D("""{ "defaultValue": {"value": "foo"} }""", true, "foo"),
//    D("""{ "defaultValue": {"value": "nil"} }""", true, "nil"),
//    D("""{ "defaultValue": {"value": "NULL"} }""", true, "NULL"),
//    D("""{ "defaultValue": {"value": "true"} }""", true, "true"),
//    D("""{ "defaultValue": {"value": 3.1} }""", true, 3.1),
//    D("""{ "defaultValue": {"value": 7} }""", true, 7),
//    D("""{ "defaultValue": {"value": false} }""", true, false),
//  TC("""{ "defaultValue": {"value": null, "quoteValue": true} }""", true, "null"),
//  TC("""{ "defaultValue": {"value": true} }""", true, true),
//
)
