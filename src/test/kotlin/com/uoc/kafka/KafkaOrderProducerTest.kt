package com.uoc.kafka

import com.uoc.domain.*
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

    companion object {
        private fun order(): Order {
            return Order(
                orderId = OrderId(),
                customerId = CustomerId(1),
                shippingAddress = AddressId(1),
                items = listOf(
                    OrderItem(productId = "ProductA", quantity = 1),
                    OrderItem(productId = "ProductB", quantity = 2)
                ),
            )
        }
    }
}