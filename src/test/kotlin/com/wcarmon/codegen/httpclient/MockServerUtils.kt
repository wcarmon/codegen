package com.wcarmon.codegen.httpclient

import org.mockserver.client.MockServerClient
import org.testcontainers.containers.MockServerContainer
import org.testcontainers.utility.DockerImageName

// See https://www.mock-server.com/where/docker.html
// See https://www.testcontainers.org/modules/mockserver/
// See dashboard at   http://localhost:<port>/mockserver/dashboard

const val MOCK_SERVER_IMAGE_VERSION = "5.11.2"


/** Construct */
fun buildMockServerContainer(
  image: DockerImageName = MOCK_SERVER_IMAGE,
): MockServerContainer =
  MockServerContainer(image)


/**
 * See https://www.mock-server.com/mock_server/mockserver_clients.html#button_client_create_expectation
 */
fun getMockConfig(
  container: MockServerContainer,
) = MockServerClient(
  container.host,
  container.serverPort,
)


private val MOCK_SERVER_IMAGE = DockerImageName
  .parse("mockserver/mockserver")
  .withTag("mockserver-${MOCK_SERVER_IMAGE_VERSION}")
