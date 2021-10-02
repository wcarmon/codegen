package com.wcarmon.codegen.ast


/**
 *
 * See https://docs.oracle.com/javase/tutorial/java/annotations/basics.html
 * See https://kotlinlang.org/docs/annotations.html
 * See https://kotlinlang.org/spec/annotations.html
 * See https://www.typescriptlang.org/docs/handbook/decorators.html
 * See https://www.digitalocean.com/community/tutorials/how-to-use-struct-tags-in-go
 */
class AnnotationExpression(
  /** can contain ":" or '[' as in "@get:Foo" or "@file:JvmName"*/
  private val name: String,
  private val params: List<Expression> = listOf(),
) : Expression {

  init {

    require(name.isNotBlank()) { "annotation name is required" }
    require(name.trim() == name) { "name must be trimmed: $name" }
    require(!name.startsWith("@")) { "Remove leading @ from annotation: $name" }

    //TODO: regex: [a-zA-z]([a-zA-z0-9:]*[a-zA-Z0-9])?
  }

  override val expressionName: String = AnnotationExpression::class.java.name

  override fun renderWithoutDebugComments(config: RenderConfig): String {
    TODO("Not yet implemented")
  }
}

// -- acceptable params
//Arrays of the types listed above
//Classes (Foo::class)
//Enums
//Other annotations
//Strings
//Types that correspond to Java primitive types (Int, Long etc.)
//key-value pairs
