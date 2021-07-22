package com.wcarmon.codegen.input

import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path


/**
 * See Pattern info: https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob
 *
 * See PathMatch info: https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)
 *
 * example: glob:<the-pattern>
 *
 * (&#47; == slash)
 */
fun getFilesForNamePattern(
  searchRoot: Path,
  filePattern: String,
): Set<Path> {

  val cleanRoot = searchRoot.normalize().toAbsolutePath()
  require(Files.isDirectory(cleanRoot)) { "Expected directory at $cleanRoot" }
  require(Files.exists(cleanRoot)) { "Expected directory at $cleanRoot" }

  require(
    filePattern.startsWith("glob:")
        || filePattern.startsWith("regex:")) {
    "java.nio.file.FileSystems only supports 'glob:' or 'regex:', filePattern=$filePattern"
  }

  val matcher = FileSystems
    .getDefault()
    .getPathMatcher(filePattern)

  val output = mutableListOf<Path>()
  val visitor = PathCollectingVisitor(matcher, output::add)
  Files.walkFileTree(cleanRoot, visitor)
  return output.map { it }.toSortedSet()
}

/**
 * Convenience function
 * Aggregation over Multiple patterns
 */
fun getFilesForNamePattern(
  searchRoot: Path,
  patterns: Collection<String>,
) = patterns
  .map { pattern ->
    getFilesForNamePattern(searchRoot, pattern)
  }
  .reduce { acc, current -> acc.union(current) }
