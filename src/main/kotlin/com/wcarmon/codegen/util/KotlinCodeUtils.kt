@file:JvmName("KotlinCodeUtils")

/** Utilities only useful for generating Kotlin */
package com.wcarmon.codegen.util

import com.wcarmon.codegen.model.BaseFieldType.USER_DEFINED
import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.Field
import com.wcarmon.codegen.model.TargetLanguage.KOTLIN_JVM_1_4

//TODO: make most of these private

@Suppress("ReturnCount")
fun kotlinTypeLiteral(
  field: Field,
  qualified: Boolean = true,
): String {

  val output = fullyQualifiedKotlinTypeLiteral(field)

  if (qualified) {
    return output
  }

  if (!field.isParameterized(KOTLIN_JVM_1_4)) {
    return output.substringAfterLast(".")
  }

  return unqualifyKotlinType(output)
}


fun getKotlinImportsForFields(entity: Entity) =
  entity.fields
    .asSequence()
    .filter {
      it.effectiveBaseType(KOTLIN_JVM_1_4) == USER_DEFINED || !it.isParameterized(KOTLIN_JVM_1_4)
    }
    .map { kotlinTypeLiteral(it) }
    .map { it.removeSuffix("?") }
    .filter { kotlinTypeRequiresImport(it) }
    .toSortedSet()


/**
 * @return true when JVM compiler cannot automatically resolve the type
 */
@Suppress("ReturnCount")
fun kotlinTypeRequiresImport(fullyQualifiedJavaType: String): Boolean {
  if (fullyQualifiedJavaType.startsWith("java.lang.")) {
    return false
  }

  //TODO: is this robust enough?
  if (fullyQualifiedJavaType.startsWith("kotlin.")) {
    return false
  }


  // primitives
  if (!fullyQualifiedJavaType.contains(".")) {
    return false
  }

  return true
}

/**
 * @return comma separated method args clause
 */
fun kotlinMethodArgsForFields(
  fields: Collection<Field>,
  qualified: Boolean,
) =
  fields.joinToString(", ") {
    "${it.name.lowerCamel}: ${kotlinTypeLiteral(it, qualified)}"
  }


//TODO: the return on investment is low here
private fun unqualifyKotlinType(fullyQualifiedKotlinType: String): String {

  //TODO: handle arrays

//  Set<com.wcarmon.chrono.model.ChronoTag>

  // eg. "java.util.Set" or "java.util.List"
  val delim = "<"
  val qualifiedUnparameterizedType = fullyQualifiedKotlinType.substringBefore(delim)

  return qualifiedUnparameterizedType.substringAfterLast(".") +
      delim +
      fullyQualifiedKotlinType.substringAfter(delim)
}
