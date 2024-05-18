package com.uoc.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.uoc.config.ConfigReader
import com.uoc.config.DatabaseConfig
import com.uoc.domain.*
import com.uoc.kafka.message.KafkaOrderRecord
import com.uoc.service.CacheEvictionService
import io.micronaut.configuration.kafka.streams.ConfiguredStreamBuilder
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.kstream.KStream
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Factory
class CacheEvictionStream(
    private val configReader: ConfigReader,
    private val cacheEvictionService: CacheEvictionService
) {
    private val log = LoggerFactory.getLogger(CacheEvictionStream::class.java)
    private val mapper = jacksonObjectMapper()

    @Singleton
    fun cacheEvictionStream(builder: ConfiguredStreamBuilder): KStream<String, String> {
        val props = builder.configuration
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.getName()
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.getName()
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        props[StreamsConfig.COMMIT_INTERVAL_MS_CONFIG] = "500"
        val source = builder.stream<String, String>(configReader.cacheEvictionTopic)

        source.foreach { _, value ->
            log.info("Received kafka string record: {}", value)
            val orderRecord = mapper.readValue(value, KafkaOrderRecord::class.java)
            log.info("Record mapped: {}", orderRecord)
            cacheEvictionService.evict(orderRecord.toDomain())
        }
        return source
    }

    companion object {
        private fun KafkaOrderRecord.toDomain(): Order {
            return Order(
                orderId = OrderId(id),
                customerId = CustomerId(customerId),
                shippingAddress = AddressId(addressId),
                status = OrderStatus.valueOf(status),
                createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneOffset.UTC),
                updatedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneOffset.UTC),
            )
        }
    }
}
