package com.wcarmon.codegen.input

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

/** Visit paths, add to sink when the matcher accepts */
class PathCollectingVisitor(
  private val matcher: PathMatcher,
  private val pathSink: (Path) -> Unit,
) : SimpleFileVisitor<Path>() {

  override fun visitFile(path: Path, attrs: BasicFileAttributes): FileVisitResult {
    if (matcher.matches(path)) {
      pathSink(path)
    }

    return CONTINUE
  }

  override fun visitFileFailed(file: Path?, exc: IOException?) =
    CONTINUE
}
