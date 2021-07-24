package com.wcarmon.codegen.model

import com.wcarmon.codegen.model.BaseFieldType.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BaseFieldTypeTest {

  // Map<TypeInJson, BaseFieldType>
  val expected: Map<String, BaseFieldType> = mapOf(
    "golang.bool" to BOOLEAN,
    "golang.byte" to INT_8,
    "golang.float32" to FLOAT_32,
    "golang.float64" to FLOAT_64,
    "java.lang.Boolean" to BOOLEAN,
    "java.lang.Byte" to INT_8,
  )

  @Test
  fun testParsing() {

    expected.forEach {
      assertEquals(it.value, BaseFieldType.parse(it.key)) {
        "Failed to parse ${it.key}"
      }
    }
  }
}
