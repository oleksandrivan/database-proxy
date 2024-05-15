package com.uoc.kafka

import com.uoc.AbstractIntegrationTest
import com.uoc.domain.OrderStatus
import com.uoc.util.Fixtures.Companion.order
import jakarta.inject.Inject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled("Only for local testing")
class KafkaOrderProducerTest : AbstractIntegrationTest() {

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
