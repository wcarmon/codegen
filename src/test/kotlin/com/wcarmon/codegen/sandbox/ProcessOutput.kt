package com.wcarmon.codegen.sandbox

import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

data class ProcessOutput(
  val stdErr: Path,
  val stdOut: Path,
) {

  fun printStdOut(printStream: PrintStream = System.out) {
    val content = String(Files.readAllBytes(stdOut))
    if (content.isBlank()) {
      return
    }

    printStream.println(content.trim())
  }

  fun printStdErr(printStream: PrintStream = System.err) {
    val content = String(Files.readAllBytes(stdErr))
    if (content.isBlank()) {
      return
    }

    printStream.println(content.trim())
  }
}
