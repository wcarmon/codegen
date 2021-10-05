package com.wcarmon.codegen.log

import org.apache.logging.log4j.message.MapMessage
import java.util.*

private const val SERIALIZED_NULL = "null"
private const val KEY_FOR_MESSAGE = "message"

fun org.apache.logging.log4j.Logger.structuredTrace(
  message: String,
  vararg pairs: Pair<String, Any?>,
) = trace(CodegenMapMessage(message, pairs))

fun org.apache.logging.log4j.Logger.structuredDebug(
  message: String,
  vararg pairs: Pair<String, Any?>,
) = debug(CodegenMapMessage(message, pairs))

fun org.apache.logging.log4j.Logger.structuredInfo(
  message: String,
  vararg pairs: Pair<String, Any?>,
) = info(CodegenMapMessage(message, pairs))

fun org.apache.logging.log4j.Logger.structuredWarn(
  message: String,
  vararg pairs: Pair<String, Any?>,
) = warn(CodegenMapMessage(message, pairs))

fun org.apache.logging.log4j.Logger.structuredError(
  message: String,
  vararg pairs: Pair<String, Any?>,
) = error(CodegenMapMessage(message, pairs))

fun org.apache.logging.log4j.Logger.structuredTrace(
  vararg pairs: Pair<String, Any?>,
) = trace(CodegenMapMessage(pairs))

fun org.apache.logging.log4j.Logger.structuredDebug(
  vararg pairs: Pair<String, Any?>,
) = debug(CodegenMapMessage(pairs))

fun org.apache.logging.log4j.Logger.structuredInfo(
  vararg pairs: Pair<String, Any?>,
) = info(CodegenMapMessage(pairs))

fun org.apache.logging.log4j.Logger.structuredWarn(
  vararg pairs: Pair<String, Any?>,
) = warn(CodegenMapMessage(pairs))

fun org.apache.logging.log4j.Logger.structuredError(
  vararg pairs: Pair<String, Any?>,
) = error(CodegenMapMessage(pairs))


internal class CodegenMapMessage(
  map: Map<String, String>
) : MapMessage<CodegenMapMessage, String>(map) {

  constructor(
    message: String,
    pairs: Array<out Pair<String, Any?>>
  ) : this(
    toLogFriendlyMap(KEY_FOR_MESSAGE to message, *pairs)
  )

  constructor(
    pairs: Array<out Pair<String, Any?>>
  ) : this(
    toLogFriendlyMap(*pairs)
  )

  override fun newInstance(map: Map<String, String>) = CodegenMapMessage(map)
}

private fun toLogFriendlyMap(
  vararg pairs: Pair<String, Any?>
) =
  pairs
    //TODO: sort message first
    .map {
      it.first to if (it.second is Array<*>) {
        Arrays.toString(it.second as Array<*>)
      } else {
        (it.second?.toString() ?: SERIALIZED_NULL)
      }
    }
    .sortedBy { it.first }
    .toMap()
