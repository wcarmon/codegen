package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Represents ...
 * - REST: url-segment
 * - Protocol buffer: packages: https://developers.google.com/protocol-buffers/docs/proto#packages
 * - RDBMS: schema
 *
 * - C#: namespace: https://docs.microsoft.com/en-us/dotnet/csharp/fundamentals/types/namespaces
 * - C++: namespace: https://en.cppreference.com/w/cpp/language/namespace
 * - C: (n/a)
 * - Golang: package name: https://golang.org/doc/effective_go#names
 * - Java: package name: https://docs.oracle.com/javase/tutorial/java/package/namingpkgs.html
 * - Kotlin: package name: https://kotlinlang.org/docs/coding-conventions.html#naming-rules
 * - Rust: namespace: https://doc.rust-lang.org/beta/reference/names/namespaces.html
 * - Typescript: namespace: https://www.typescriptlang.org/docs/handbook/namespaces-and-modules.html#using-namespaces
 */
data class PackageName(
  @JsonValue
  val value: String,
) {

  companion object {
    @JvmStatic
    @JsonCreator
    fun fromString(value: String) = PackageName(value)
  }

  init {
    require(value == value.trim()) {
      "package names must be trimmed: '$value'"
    }

    //TODO: max length
    // TODO: restrictions (allow blank)
  }

  override fun toString() = value
}
