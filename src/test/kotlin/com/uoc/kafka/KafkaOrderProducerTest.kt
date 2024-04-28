package com.uoc.kafka

import com.uoc.domain.*
import com.uoc.util.Fixtures.Companion.order
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test

@MicronautTest
class KafkaOrderProducerTest {

    @Inject
    lateinit var kafkaOrderProducer: KafkaOrderProducer

    @Test
    fun produceUserMessage() {
        kafkaOrderProducer.storeOrder(order())
    }
}