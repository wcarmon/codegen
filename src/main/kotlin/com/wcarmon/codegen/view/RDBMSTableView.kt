package com.wcarmon.codegen.view

import com.wcarmon.codegen.model.Entity
import com.wcarmon.codegen.model.TargetLanguage
import com.wcarmon.codegen.model.util.commaSeparatedColumnAssignment
import com.wcarmon.codegen.model.util.commaSeparatedColumns
import com.wcarmon.codegen.model.util.primaryKeyTableConstraint
import org.atteo.evo.inflector.English

/**
 * RDBMS related convenience methods for a [Entity]
 * See [com.wcarmon.codegen.model.RDBMSTableConfig]
 */
data class RDBMSTableView(
  private val entity: Entity
) {

  val primaryKeyFields = entity.fields
    .filter { it.rdbms.positionInPrimaryKey != null }
    .sortedBy { it.rdbms.positionInPrimaryKey!! }

  val nonPrimaryKeyFields = entity.fields
    .filter { it.rdbms.positionInPrimaryKey == null }
    .sortedBy { it.name.lowerCamel }

  val commaSeparatedColumns = commaSeparatedColumns(entity)

  //TODO: return Documentation
  val commentForPKFields =
    if (primaryKeyFields.isEmpty()) ""
    else "PrimaryKey " + English.plural("field", primaryKeyFields.size)

  val dbSchemaPrefix =
    if (entity.rdbms.schema.isBlank() != false) ""
    else "${entity.rdbms.schema}."

  val hasNonPrimaryKeyFields = nonPrimaryKeyFields.isNotEmpty()

  val hasPrimaryKeyFields = primaryKeyFields.isNotEmpty()

  val primaryKeyWhereClause = commaSeparatedColumnAssignment(primaryKeyFields)

  val primaryKeyTableConstraint = primaryKeyTableConstraint(entity)

  val questionMarkStringForInsert = (1..entity.fields.size).joinToString { "?" }


  //TODO: more here
}
