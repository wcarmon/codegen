package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Represents ...
 * - REST: url-segment
 * - Protocol buffer: packages: https://developers.google.com/protocol-buffers/docs/proto#packages
 * - RDBMS: schemas
 *
 * - Kotlin: package name: https://kotlinlang.org/docs/coding-conventions.html#naming-rules
 * - Java: package name: https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html
 * - Golang: package name: https://golang.org/doc/effective_go#names
 * - Rust: namespace: https://doc.rust-lang.org/beta/reference/names/namespaces.html
 * - c: (n/a)
 * - c++: namespace: https://en.cppreference.com/w/cpp/language/namespace
 * - Typescript: namespace: https://www.typescriptlang.org/docs/handbook/namespaces-and-modules.html#using-namespaces
 */
data class PackageName(
  @JsonValue
  val value: String,
) {

  companion object {
    @JvmStatic
    @JsonCreator
    fun build(value: String) = PackageName(value)
  }

  init {
    require(value == value.trim()) {
      "package names must be trimmed: '$value'"
    }

    // TODO: restrictions (allow blank)
  }
}
