package com.wcarmon.codegen.sandbox

import org.apache.logging.log4j.LogManager
import java.nio.file.Files
import java.nio.file.Path
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
) {
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

  val process = ProcessBuilder(command)
    .inheritIO()
    .directory(workingDir.toFile())
    .start()

  //TODO: stream stdOut to current logger
  val processTerminated = process.waitFor(
    maxWait.seconds, TimeUnit.SECONDS)

//  val strErr = String(process.errorStream.readAllBytes())
//  val strOut = String(process.inputStream.readAllBytes())

  check(processTerminated) {
    "Process timed out: " +
        "workingDir=$workingDir, " +
        "command=$command"
//        "strErr=$strErr, " +
//        "strOut=$strOut"
  }

  check(process.exitValue() == 0) {
    "gradle wrapper command failed: " +
        "workingDir=$workingDir, " +
        "command=$command, " +
        "exitValue=$process.exitValue(), "
//        "strErr=$strErr, " +
//        "strOut=$strOut"
  }

//  if (strOut.isNotBlank()) {
//    LOG.debug("stdOut=$strOut")
//  }
//
//  if (strErr.isNotBlank()) {
//    LOG.debug("strErr=$strErr")
//  }
}
