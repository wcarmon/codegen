@file:JvmName("JavaCodeUtils")

/** Utilities only useful for generating Java */
package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*
import com.wcarmon.codegen.model.ast.Expression
import com.wcarmon.codegen.model.ast.RawStringExpression

/**
 * @return semicolon terminated statements to execute preconditions
 */
fun buildJavaPreconditionStatements(fields: Collection<Field>): Set<String> {

  val output = mutableSetOf<String>()

  fields.forEach { field ->

    if (!field.usesStringValidation && !field.type.nullable) {
      output +=
        "Objects.requireNonNull(${field.name.lowerCamel}, \"${field.name.lowerCamel} is required and null.\");"
    }

    if (field.usesStringValidation) {
      output +=
        "Preconditions.checkArgument(StringUtils.isNotBlank(${field.name.lowerCamel}), \"${field.name.lowerCamel} is required and blank.\")"
    }
  }

  return output.toSortedSet()
}


//TODO: document me
fun commaSeparatedJavaFields(
  fields: Collection<Field>,
) =
  fields.map { it.name.lowerCamel }
    .joinToString(", ")

/**
 * @return comma separated method args clause
 */
fun commaSeparatedJavaMethodArgs(
  fields: Collection<Field>,
  qualified: Boolean,
) =
  fields.joinToString(", ") {
    "${javaTypeLiteral(it.type, qualified)} ${it.name.lowerCamel}"
  }


/**
 * Useful for Jackson, and json stores (eg. ElasticSearch, MongoDB, ...)
 *
 * See [com.wcarmon.codegen.model.Serde]
 * See [com.wcarmon.codegen.model.RDBMSColumn.serde]
 */
//private fun getJavaDeserializeTemplate(field: Field): ExpressionTemplate =
//  if (field.jvm.serde != null) {
//    field.jvm.serde.deserialize
//
//  } else if (field.type.base.isTemporal
//    || field.type.base in setOf(PATH, URI, URL)
//    || field.type.enumType
//  ) {
//    defaultJavaDeserializeTemplate(field.type)
//
//  } else {
//    //TODO: identity?
//    TODO("decide how to deserialize on jvm: ${field.type}")
//  }


/**
 * primitive and java.lang classes are skipped
 *
 * @return distinct, sorted, fully qualified classes, ready for import
 */
fun javaImportsForFields(entity: Entity) =
  entity.fields
    .filter { it.effectiveBaseType == USER_DEFINED || !it.type.isParameterized }
    .map { javaTypeLiteral(it.type) }
    .filter { javaTypeRequiresImport(it) }
    .toSortedSet()

/**
 * See https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
 *
 * @return literal for Java type
 */
fun javaTypeLiteral(
  type: LogicalFieldType,
  qualified: Boolean = true,
): String {

  val output = fullyQualifiedJavaTypeLiteral(type)

  if (qualified) {
    return output
  }

  if (!type.isParameterized) {
    return output.substringAfterLast(".")
  }

  return unqualifyJavaType(output)
}

/**
 * Template should prefix "new" when required
 * Statement terminators (semicolons) must be handled by caller
 *
 * @return literal for mutable java collection factory
 */
fun newJavaCollectionExpression(type: LogicalFieldType): String {

  check(type.base.isCollection) {
    "I can only instantiate native collections: failedType=$type"
  }

  return when (type.base) {
    ARRAY -> TODO("Handle creating arrays (need to know size)")
    LIST -> "ArrayList<>()"
    MAP -> "HashMap<>()"
    SET -> "HashSet<>()"
    else -> TODO("Handle instantiating: $type")
  }
}


//TODO: handle enums

/**
 * Builds java.lang.Object.equals based comparison expression
 * Useful in POJOs
 *
 * @return expression for java equality test (for 1 field)
 */
fun javaEqualityExpression(
  type: LogicalFieldType,
  fieldName: Name,
  identifier0: String,
  identifier1: String,
): Expression {
  require(identifier0.isNotBlank())
  require(identifier1.isNotBlank())

  if (type.enumType || type.base == BOOLEAN || type.base == CHAR) {
    return RawStringExpression("$identifier0.${fieldName.lowerCamel} == $identifier1.${fieldName.lowerCamel}")
  }

  if (type.base == FLOAT_64) {
    return RawStringExpression(
      "Double.compare($identifier0.${fieldName.lowerCamel}, $identifier1.${fieldName.lowerCamel}) == 0")
  }

  if (type.base == FLOAT_32) {
    return RawStringExpression(
      "Float.compare($identifier0.${fieldName.lowerCamel}, $identifier1.${fieldName.lowerCamel}) == 0")
  }

  if (type.base == ARRAY) {
    return RawStringExpression(
      "Arrays.deepEquals($identifier0.${fieldName.lowerCamel}, $identifier1.${fieldName.lowerCamel})")
  }

  return RawStringExpression(
    "Objects.equals($identifier0.${fieldName.lowerCamel}, $identifier1.${fieldName.lowerCamel})")
}

/**
 * @return template which invokes creates an unmodifiable version of the collection
 */
//TODO: use %s template approach
fun unmodifiableJavaCollectionMethod(base: BaseFieldType): String {
  require(base.isCollection) {
    "method only for collections: $base"
  }

  return when (base) {
    LIST -> "Collections.unmodifiableList"
    MAP -> "Collections.unmodifiableMap"
    SET -> "Collections.unmodifiableSet"
    else -> TODO("Handle immutable version of: $base")
  }
}

private fun fullyQualifiedJavaTypeLiteral(
  type: LogicalFieldType,
): String = when (type.base) {

  ARRAY -> type.typeParameters.first() + "[]"
  BOOLEAN -> if (type.nullable) "Boolean" else "boolean"
  CHAR -> if (type.nullable) "Character" else "char"
  DURATION -> "java.time.Duration"
  FLOAT_32 -> if (type.nullable) "Float" else "float"
  FLOAT_64 -> if (type.nullable) "Double" else "double"
  FLOAT_BIG -> "java.math.BigDecimal"
  INT_128 -> "java.math.BigInteger"
  INT_16 -> if (type.nullable) "Short" else "short"
  INT_32 -> if (type.nullable) "Integer" else "int"
  INT_64 -> if (type.nullable) "Long" else "long"
  INT_8 -> if (type.nullable) "Byte" else "byte"
  INT_BIG -> "java.math.BigInteger"
  LIST -> "java.util.List<${type.typeParameters[0]}>"
  MAP -> "java.util.Map<${type.typeParameters[0]}, ${type.typeParameters[1]}>"
  MONTH_DAY -> "java.time.MonthDay"
  PATH -> "java.nio.file.Path"
  PERIOD -> "java.time.Period"
  SET -> "java.util.Set<${type.typeParameters[0]}>"
  STRING -> "String"
  URI -> "java.net.URI"
  URL -> "java.net.URL"
  UTC_INSTANT -> "java.time.Instant"
  UTC_TIME -> "java.time.OffsetTime"
  UUID -> "java.util.UUID"
  YEAR -> "java.time.Year"
  YEAR_MONTH -> "java.time.YearMonth"
  ZONE_AGNOSTIC_DATE -> "java.time.LocalDate"
  ZONE_AGNOSTIC_TIME -> "java.time.LocalTime"
  ZONE_OFFSET -> "java.time.ZoneOffset"
  ZONED_DATE_TIME -> "java.time.ZonedDateTime"

  //TODO: need to convert when raw is specified in json as non-jvm
  USER_DEFINED -> type.rawTypeLiteral
}

/**
 * @return true when JVM compiler cannot automatically resolve the type
 */
private fun javaTypeRequiresImport(fullyQualifiedJavaType: String): Boolean {
  if (fullyQualifiedJavaType.startsWith("java.lang")) {
    return false
  }

  // primitives
  if (!fullyQualifiedJavaType.contains(".")) {
    return false
  }

  return true
}

//TODO: the return on investment is low here
private fun unqualifyJavaType(fullyQualifiedJavaType: String): String {

  //TODO: handle arrays

  // eg. "java.util.Set" or "java.util.List"
  val delim = "<"
  val qualifiedUnparameterizedType = fullyQualifiedJavaType.substringBefore(delim)

  return qualifiedUnparameterizedType.substringAfterLast(".") +
      delim +
      fullyQualifiedJavaType.substringAfter(delim)
}
