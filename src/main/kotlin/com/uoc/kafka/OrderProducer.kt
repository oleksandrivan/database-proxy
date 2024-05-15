package com.uoc.kafka

import com.uoc.config.ConfigReader
import com.uoc.domain.Order
import com.uoc.domain.OrderId
import com.uoc.domain.OrderItem
import com.uoc.kafka.message.*
import io.micronaut.configuration.kafka.annotation.KafkaClient
import jakarta.inject.Singleton
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.time.format.DateTimeFormatter

interface OrderProducer {
    fun storeOrder(order: Order)
}

typealias CustomRecord = ProducerRecord<String, Message<Payload, Schema>>

@Singleton
class KafkaOrderProducer(
    private val configReader: ConfigReader,
    @KafkaClient("order-producer") private val kafkaProducer: Producer<String, Message<Payload, Schema>>
) : OrderProducer {

    override fun storeOrder(order: Order) {
        val orderMessage = Message(OrderSchema.instance(), order.toPayload())
        val orderItemsMessages =
            order.items.map { Message(OrderItemSchema.instance(), it.toPayload(order.orderId)) }
        val orderRecord = orderMessage.toRecord(order.orderId, configReader)
        val orderItemsRecords = orderItemsMessages.map { it.toRecord(order.orderId, configReader) }
        kafkaProducer.send(orderRecord)
        orderItemsRecords.forEach { record -> kafkaProducer.send(record) }
    }

    companion object {
        private fun Order.toPayload(): OrderPayload {
            return OrderPayload(
                id = orderId.value,
                customerId = customerId.value,
                addressId = shippingAddress.value,
                status = status.name,
                createdAt = createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
                updatedAt = updatedAt.format(DateTimeFormatter.ISO_DATE_TIME)
            )
        }

        private fun OrderItem.toPayload(orderId: OrderId): OrderItemPayload {
            return OrderItemPayload(
                orderId = orderId.value,
                productId = productId,
                quantity = quantity
            )
        }

        private fun <T : Payload, S : Schema> Message<T, S>.toRecord(
            orderId: OrderId, configReader: ConfigReader
        ): CustomRecord {
            return when (schema) {
                is OrderSchema -> ProducerRecord(
                    configReader.ordersTopic,
                    orderId.value,
                    this
                ) as CustomRecord

                is OrderItemSchema -> ProducerRecord(
                    configReader.orderItemsTopic,
                    orderId.value,
                    this
                ) as CustomRecord

                else -> throw IllegalArgumentException("Unknown schema")
            }
        }
    }
}
