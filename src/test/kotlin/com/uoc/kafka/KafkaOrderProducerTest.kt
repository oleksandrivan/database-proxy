package com.uoc.kafka

import com.uoc.domain.OrderStatus
import com.uoc.util.Fixtures.Companion.order
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

@MicronautTest
class KafkaOrderProducerTest {

    @Inject
    lateinit var kafkaOrderProducer: KafkaOrderProducer

    @Test
    fun produceOrderRecord() {
        kafkaOrderProducer.storeOrder(order())
    }

    @Test
    fun produceAndUpdateOrderRecord() {
        val order = order()
        kafkaOrderProducer.storeOrder(order)
        kafkaOrderProducer.storeOrder(order.copy(status = OrderStatus.DELIVERED))
    }
}
