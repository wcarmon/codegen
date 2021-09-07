import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.testcontainers.containers.KafkaContainer
import java.util.concurrent.ThreadLocalRandom


private val DEFAULT_PROPS = mapOf(
  ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
  ConsumerConfig.CLIENT_ID_CONFIG to "test-consumer-id-for-streams",
  ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
  ConsumerConfig.GROUP_ID_CONFIG to "group-" + ThreadLocalRandom.current().nextInt(100),
  ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.getName(),
  ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.getName(),

  ProducerConfig.CLIENT_ID_CONFIG to "test-producer-id-for-streams",
  ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.getName(),
  ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java.getName(),
)

fun buildKafkaProducer(
  container: KafkaContainer,
  overrides: Map<String, Any> = mapOf(),
): KafkaProducer<String, String> {

  val props = mapOf<String, Any>(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to container.bootstrapServers,
  ) +
      DEFAULT_PROPS +
      overrides

  return KafkaProducer<String, String>(props)
}

fun buildKafkaConsumer(
  container: KafkaContainer,
  overrides: Map<String, Any> = mapOf(),
): KafkaConsumer<String, String> {

  val props = mutableMapOf<String, Any>(
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to container.bootstrapServers,
  ) +
      DEFAULT_PROPS +
      overrides

  return KafkaConsumer(props)
}
