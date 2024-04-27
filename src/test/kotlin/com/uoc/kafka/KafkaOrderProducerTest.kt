package com.uoc.kafka

import com.uoc.kafka.message.User
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

@MicronautTest
class KafkaOrderProducerTest {

    @Inject
    lateinit var kafkaOrderProducer: KafkaOrderProducer

    @Test
    fun produceUserMessage() {
        val user = User(100L, "JohnDoe", "EU", "ALPHAMALE")
        kafkaOrderProducer.produceUserMessage(user)
    }
}