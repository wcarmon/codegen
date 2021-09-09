package com.wcarmon.codegen.httpclient

import com.fasterxml.jackson.databind.ObjectWriter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okio.BufferedSink

class JSONRequestBody(
  val serializeMe: Any,
  val objectWriter: ObjectWriter,
) : RequestBody() {

  override fun contentType() = "application/json".toMediaType()

  override fun writeTo(sink: BufferedSink) {
    sink.write(objectWriter.writeValueAsBytes(serializeMe))
  }
}
