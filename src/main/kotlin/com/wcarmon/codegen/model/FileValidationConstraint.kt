package com.wcarmon.codegen.model

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Validation constraints for files & directories
 *
 * See src/main/resources/json-schema/field-validation.schema.json
 */
enum class FileValidationConstraint(
  @JsonValue val value: String,
) {
  DIR_IF_EXISTS("dir-if-exists"),
  EXISTING_DIR("existing-dir"),
  EXISTING_FILE("existing-file"),
  FILE_IF_EXISTS("file-if-exists"),
  ;
}
