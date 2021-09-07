package com.wcarmon.codegen.integration

import buildKafkaConsumer
import buildKafkaProducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom


@Testcontainers
internal class KafkaTest {

  lateinit var consumer: KafkaConsumer<String, String>
  lateinit var producer: KafkaProducer<String, String>

  companion object {

    private val IMAGE =
      DockerImageName.parse("confluentinc/cp-kafka:6.2.0")

    private lateinit var container: KafkaContainer

    @BeforeAll
    @JvmStatic
    fun beforeClass() {
      container = KafkaContainer(IMAGE)
      container.start()
    }

    @AfterAll
    fun afterClass() = container.stop()
  }

  @BeforeEach
  fun setUp() {
    consumer = buildKafkaConsumer(container)
    producer = buildKafkaProducer(container)
  }

  @Test
  fun foo() {
    val topic = "topic-01"
    val message = "test-key-00" to "test-message-body-${ThreadLocalRandom.current()}"

    consumer.subscribe(listOf(topic))

    runBlocking {

      // -- Consume
      launch(Dispatchers.IO) {
        repeat(8) {

          val consumerRecords = consumer.poll(Duration.ofSeconds(1))

          consumerRecords.forEach { record ->
            assertEquals(topic, record.topic())
            assertEquals(message.first, record.key())
            assertEquals(message.second, record.value())

            consumer.commitSync()
            consumer.close()
            return@launch
          }
        }

        fail("KafkaConsumer never received ConsumerRecords")
      }

      // -- Produce
      launch(Dispatchers.Default) {
        val future = producer.send(
          ProducerRecord(topic, message.first, message.second))
        future.get()
      }
    }
  }
}
