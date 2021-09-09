package com.wcarmon.codegen.httpclient

import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URL

/** Example of generated HTTP client using OkHttp */
internal class ExampleGeneratedHTTPClient(
  val baseUrl: URL,
  val objectReader: ObjectReader,
  val okHttpClient: OkHttpClient,
  val objectWriter: ObjectWriter,
) {

  fun doCreateEntity(entity: Any) {

    val body = JSONRequestBody(
      objectWriter = objectWriter,
      serializeMe = entity,
    )

    val request = Request.Builder()
      .post(body)
      .url("$baseUrl/chrono-board")
      .build()

    okHttpClient
      .newCall(request)
      .execute()
      .use { resp ->
        if (resp.isSuccessful) {
          //TODO: do something useful here
          println("TODO: handle success here")

        } else {
          //TODO: throw WebException here
          // (so callers can access response code and message)
          println("TODO: failure success here")
        }
      }
  }
}
