package com.wcarmon.codegen.sandbox

import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.io.path.absolute

private val LOG = LogManager.getLogger("ProcessUtils")


/**
 * A simpler API over java ProcessAPI
 *
 * Throws for non-zero exit code
 */
fun executeCommand(
  command: List<String>,
  maxWait: Duration = Duration.ofSeconds(45),
  rawWorkingDir: Path,
): ProcessOutput {
  require(command.isNotEmpty()) { "command cannot be empty" }
  require(!maxWait.isNegative) { "maxWait must be positive" }

  val workingDir = rawWorkingDir.normalize().absolute()
  require(Files.exists(workingDir)) {
    "working directory must exist at $workingDir"
  }

  LOG.debug("Executing command: " +
      "workingDir=$workingDir, " +
      "command=$command, " +
      "maxWait=$maxWait"
  )

  val tempDir = Files.createTempDirectory("spawned-command.")
  val stdErr = Files.createFile(Paths.get(tempDir.toString(), "stdErr.log"))
  val stdOut = Files.createFile(Paths.get(tempDir.toString(), "stdOut.log"))
  LOG.debug("See process output: stdOut=$stdOut, stdErr=$stdErr")

  val returnMe = ProcessOutput(
    stdErr = stdErr,
    stdOut = stdOut
  )

  val process = ProcessBuilder(command)
    //.inheritIO()  // Streams nicely, but you cannot return output/error
    .directory(workingDir.toFile())
    .redirectError(stdErr.toFile())
    .redirectOutput(stdOut.toFile())
    .start()

  val processTerminated = process.waitFor(
    maxWait.seconds, TimeUnit.SECONDS)

  if (!processTerminated) {
    returnMe.printStdOut()
    returnMe.printStdErr()

    throw IllegalStateException(
      "Process timed out: " +
          "workingDir=$workingDir, " +
          "command=$command"
    )
  }


  if (process.exitValue() != 0) {
    returnMe.printStdOut()
    returnMe.printStdErr()

    throw IllegalStateException(
      "command failed: " +
          "workingDir=$workingDir, " +
          "command=$command, " +
          "exitValue=$process.exitValue(), "
    )
  }

  return returnMe
}
