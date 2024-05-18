package com.uoc.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.uoc.config.ConfigReader
import com.uoc.domain.Order
import com.uoc.kafka.message.KafkaOrderRecord
import io.micronaut.configuration.kafka.annotation.KafkaClient
import jakarta.inject.Singleton
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.time.ZoneOffset

@Singleton
class TestKafkaPublisher(
    private val configReader: ConfigReader,
    @KafkaClient("test-producer") private val kafkaProducer: Producer<String, String>
) {
    private val jacksonObjectMapper = jacksonObjectMapper()

    fun sendTestRecord(order: Order) {
        val value = order.toKafkaRecord()
        kafkaProducer.send(
            ProducerRecord(
                configReader.cacheEvictionTopic,
                jacksonObjectMapper.writeValueAsString(value)
            )
        )
    }

    companion object {

        private fun Order.toKafkaRecord(): KafkaOrderRecord =
            KafkaOrderRecord(
                id = orderId.value,
                customerId = customerId.value,
                addressId = shippingAddress.value,
                status = status.name,
                createdAt = createdAt.toInstant(ZoneOffset.UTC).toEpochMilli(),
                updatedAt = updatedAt.toInstant(ZoneOffset.UTC).toEpochMilli()
            )
    }
}

