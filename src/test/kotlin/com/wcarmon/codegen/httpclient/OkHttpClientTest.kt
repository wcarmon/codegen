package com.wcarmon.codegen.httpclient

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse
import org.testcontainers.containers.MockServerContainer
import java.net.URL

//@Testcontainers
internal class OkHttpClientTest {

  companion object {
    private lateinit var container: MockServerContainer

    @BeforeAll
    @JvmStatic
    fun beforeClass() {
      container = buildMockServerContainer()
      container.start()
    }

    @AfterAll
    @JvmStatic
    fun afterClass() = container.stop()
  }

  lateinit var subject: ExampleGeneratedHTTPClient

  @BeforeEach
  fun setUp() {
    checkNotNull(container)

    val objectMapper = ObjectMapper()
    val okhttpClient = OkHttpClient()

    subject = ExampleGeneratedHTTPClient(
      baseUrl = URL("http://${container.host}:${container.serverPort}/api/v1"),
      objectReader = objectMapper.reader(),
      objectWriter = objectMapper.writer(),
      okHttpClient = okhttpClient,
    )
  }

  @Test
  fun aaa() {

    // -- Arrange
    val uri = "/api/v1/chrono-board"

    val mockConfig = getMockConfig(container)
    mockConfig
      .`when`(
        HttpRequest.request()
//        .withBody("""{"entity":7}""")
          .withMethod("POST")
          .withPath(uri)
      )
      .respond(
        HttpResponse.response()
          .withBody("whatever")
          .withStatusCode(201)
      )
//        .verify(
//          request()
//            .withPath("$baseUrl/chrono-board"),
//          VerificationTimes.atLeast(2)
//        )

    val entity = "foo"


    // -- Act
    subject.doCreateEntity(entity)


    // -- Assert
    val recorded = mockConfig.retrieveRecordedRequests(
      request()
        .withMethod("POST")
        .withPath(uri)
    )

    assertEquals(1, recorded.size)
    assertTrue(recorded[0] is HttpRequest)

    val recordedReq = recorded[0] as HttpRequest
    //TODO: something like this
//    assertEquals("""{"a": 123}""", recordedReq.body)

    //TODO: clear things on the mock server (or run tests one at a time or make multiple servers)
  }
}
