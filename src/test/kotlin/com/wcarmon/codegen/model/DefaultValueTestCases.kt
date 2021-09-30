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

  // ---------------------------------------------------
  // -- Assume Absent, no default
  TC(
    jvmConfigJSON = """{ }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = false,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{"defaultValue": null}""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = false,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"quoteValue": false} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = false,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"quoteValue": true} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = false,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = false,
    expectShouldQuote = false,
  ),


  // ---------------------------------------------------
  // -- Assume Present, Assume: null/nil/NULL literal
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "null", "quoteValue": false} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = true,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": null, "quoteValue": false} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = true,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": null} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = true,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "NULL", "quoteValue": false} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = true,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "nil", "quoteValue": false} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = false,
    expectIsNullLiteral = true,
    expectPresent = true,
    expectShouldQuote = false,
  ),

  // ---------------------------------------------------
  // -- Assume Present: empty collection
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": []} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = true,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": [], "quoteValue": false} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = true,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": {}} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = true,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": {}, "quoteValue": false} }""",
    expectedUninterpetedValue = null,
    expectEmptyCollection = true,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),


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
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "nil"}}""",
    expectedUninterpetedValue = "nil",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "NULL", "quoteValue": true}}""",
    expectedUninterpetedValue = "NULL",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "NULL"}}""",
    expectedUninterpetedValue = "NULL",
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
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "\"null\""} }""",
    expectedUninterpetedValue = "\"null\"",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "\"null\"", "quoteValue": true} }""",
    expectedUninterpetedValue = "\"null\"",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "\"null\"", "quoteValue": false} }""",
    expectedUninterpetedValue = "\"null\"",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": true} }""",
    expectedUninterpetedValue = true,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": true, "quoteValue": true} }""",
    expectedUninterpetedValue = "true",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": true, "quoteValue": false} }""",
    expectedUninterpetedValue = true,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": false} }""",
    expectedUninterpetedValue = false,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": false, "quoteValue": true} }""",
    expectedUninterpetedValue = "false",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": false, "quoteValue": false} }""",
    expectedUninterpetedValue = false,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "true"} }""",
    expectedUninterpetedValue = "true",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "true", "quoteValue": true} }""",
    expectedUninterpetedValue = "true",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  //Too complex to support destringify
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": "true", "quoteValue": false} }""",
//    expectedUninterpetedValue = true,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = false,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "false"} }""",
    expectedUninterpetedValue = "false",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "false", "quoteValue": true} }""",
    expectedUninterpetedValue = "false",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  //Too complex to support destringify
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": "false", "quoteValue": false} }""",
//    expectedUninterpetedValue = false,
//    expectEmptyCollection = false,
//    expectIsNullLiteral = false,
//    expectPresent = true,
//    expectShouldQuote = false,
//  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": 7} }""",
    expectedUninterpetedValue = 7,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": 7, "quoteValue": true} }""",
    expectedUninterpetedValue = "7",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": 7, "quoteValue": false} }""",
    expectedUninterpetedValue = 7,
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
  // -=-=-
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "7"} }""",
    expectedUninterpetedValue = "7",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "7", "quoteValue": true} }""",
    expectedUninterpetedValue = "7",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = true,
  ),
  //Too complex to support destringify
//  TC(
//    jvmConfigJSON = """{ "defaultValue": {"value": "7", "quoteValue": false} }""",
//    expectedUninterpetedValue = "false",
//    expectEmptyCollection = false,
//    expectIsNullLiteral = false,
//    expectPresent = true,
//    expectShouldQuote = true,
//  ),
  TC(
    jvmConfigJSON = """{ "defaultValue": {"value": "MyEnum.FOO", "quoteValue": false} }""",
    expectedUninterpetedValue = "MyEnum.FOO",
    expectEmptyCollection = false,
    expectIsNullLiteral = false,
    expectPresent = true,
    expectShouldQuote = false,
  ),
)
