@file:JvmName("JavaCodeUtils")

/** Utilities only useful for generating Java */
package com.wcarmon.codegen.model.util

import com.wcarmon.codegen.model.*
import com.wcarmon.codegen.model.BaseFieldType.*

/**
 * See https://docs.oracle.com/javase/tutorial/java/nutsandbolts/datatypes.html
 *
 * @return literal for Java type
 */
fun asJava(
  type: LogicalFieldType,
  qualified: Boolean = true,
): String {

  val output = asFullyQualifiedJava(type)

  if (qualified) {
    return output
  }

  if (!type.base.isParameterized) {
    return output.substringAfterLast(".")
  }

  return unqualifyJavaType(output)
}


fun asFullyQualifiedJava(
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
 * Deserializer: Converts from String to the type
 *
 * See [LogicalFieldType.jvmDeserializerTemplate]
 *
 * @returns jvm expression, uses %s as placeholder for field value
 */
fun defaultJavaDeserializerTemplate(type: LogicalFieldType): String = when (type.base) {
  BOOLEAN -> TODO("fix string deserializer for $type.base")
  CHAR -> TODO("fix string deserializer for $type.base")
  FLOAT_32 -> TODO("fix string deserializer for $type.base")
  FLOAT_64 -> TODO("fix string deserializer for $type.base")
  FLOAT_BIG -> TODO("fix string deserializer for $type.base")
  INT_128 -> TODO("fix string deserializer for $type.base")
  INT_BIG -> TODO("fix string deserializer for $type.base")

  ZONE_AGNOSTIC_DATE -> TODO("fix string deserializer for $type.base")
  ZONE_AGNOSTIC_TIME -> TODO("fix string deserializer for $type.base")
  ZONE_OFFSET -> TODO("fix string deserializer for $type.base")
  ZONED_DATE_TIME -> TODO("fix string deserializer for $type.base")
  MAP -> TODO("fix string deserializer for $type.base")

  // TODO: JSON serialized via Jackson
  ARRAY -> TODO("fix string deserializer for $type.base")
  LIST -> TODO("fix string deserializer for $type.base")
  SET -> TODO("fix string deserializer for $type.base")

  INT_16 -> "Short.parseShort(%s)"
  INT_32 -> "Integer.parseInt(%s)"
  INT_64 -> "Long.parseLong(%s)"
  INT_8 -> "Byte.parseByte(%s)"
  PATH -> "${asJava(type)}.of(%s)"
  STRING -> "String.valueOf(%s)"
  URI -> "${asJava(type)}.create(%s)"
  URL -> "new ${asJava(type)}(%s)"
  UUID -> "${asJava(type)}.fromString(%s)"

  DURATION,
  MONTH_DAY,
  PERIOD,
  USER_DEFINED,
  UTC_INSTANT,
  UTC_TIME,
  YEAR,
  YEAR_MONTH,
  -> "${asJava(type)}.parse(%s)"
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
): String {
  require(identifier0.isNotBlank())
  require(identifier1.isNotBlank())

  if (type.enumType || type.base == BOOLEAN || type.base == CHAR) {
    return "$identifier0.${fieldName.lowerCamel} == $identifier1.${fieldName.lowerCamel}"
  }

  if (type.base == FLOAT_64) {
    return "Double.compare($identifier0.${fieldName.lowerCamel}, $identifier1.${fieldName.lowerCamel}) == 0"
  }

  if (type.base == FLOAT_32) {
    return "Float.compare($identifier0.${fieldName.lowerCamel}, $identifier1.${fieldName.lowerCamel}) == 0"
  }

  if (type.base == ARRAY) {
    return "Arrays.deepEquals($identifier0.${fieldName.lowerCamel}, $identifier1.${fieldName.lowerCamel})"
  }

  return "Objects.equals($identifier0.${fieldName.lowerCamel}, $identifier1.${fieldName.lowerCamel})"
}


/**
 * Template should prefix "new" when required
 * Statement terminators (semicolons) must be handled by caller
 *
 * @return literal for mutable java collection factory
 */
fun newJavaCollectionExpression(base: BaseFieldType): String {

  require(base.isCollection) {
    "method only for collections: $base"
  }

  return when (base) {
    ARRAY -> TODO("Handle creating arrays (need to know size)")
    LIST -> "ArrayList<>()"
    MAP -> "HashMap<>()"
    SET -> "HashSet<>()"
    else -> TODO("Handle instantiating: $base")
  }
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

/**
 * primitive and java.lang classes are skipped
 *
 * @return distinct, sorted, fully qualified classes, ready for import
 */
fun getJavaImportsForFields(entity: Entity) =
  entity.fields
    .filter { it.type.base == USER_DEFINED || !it.type.base.isParameterized }
    .map { asJava(it.type) }
    .filter { javaTypeRequiresImport(it) }
    .toSortedSet()

/**
 * @return true when JVM compiler cannot automatically resolve the type
 */
fun javaTypeRequiresImport(fullyQualifiedJavaType: String): Boolean {
  if (fullyQualifiedJavaType.startsWith("java.lang")) {
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
fun javaMethodArgsForFields(
  fields: Collection<Field>,
  qualified: Boolean,
) =
  fields
    .map {
      "${asJava(it.type, qualified)} ${it.name.lowerCamel}"
    }
    .joinToString(", ")

/**
 * @return semicolon terminated statements to execute preconditions
 */
fun buildJavaPreconditionStatements(fields: Collection<Field>): Set<String> {

  val output = mutableSetOf<String>()

  fields.forEach { field ->

    if (field.type.base != STRING && !field.type.nullable) {
      output +=
        "Objects.requireNonNull(${field.name.lowerCamel}, \"${field.name.lowerCamel} is required and null.\");"
    }

    if (field.type.base == STRING) {
      output +=
        "Preconditions.checkArgument(StringUtils.isNotBlank(${field.name.lowerCamel}), \"${field.name.lowerCamel} is required and blank.\")"
    }
  }

  return output.toSortedSet()
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
