package com.wcarmon.codegen.input

import java.io.IOException
import java.nio.file.*
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.Consumer

/**
 * Visit paths, add to sink when the matcher accepts
 * Useful with [Files.walkFileTree]
 *
 * See PathMatcher info at:
 *  https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)
 */
class PathCollectingVisitor(
  private val matcher: PathMatcher,
  private val pathSink: Consumer<Path>,
) : SimpleFileVisitor<Path>() {

  override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
    if (matcher.matches(path)) {
      pathSink.accept(path)
    }

    return CONTINUE
  }

  override fun visitFileFailed(file: Path?, exc: IOException?) =
    CONTINUE
}
