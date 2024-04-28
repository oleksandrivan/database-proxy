package com.uoc.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.uoc.config.ConfigReader
import com.uoc.domain.Order
import com.uoc.domain.OrderId
import com.uoc.domain.OrderItem
import com.uoc.kafka.message.*
import io.micronaut.configuration.kafka.annotation.KafkaClient
import jakarta.inject.Singleton
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

interface OrderProducer {
    fun storeOrder(order: Order)
}

typealias CustomRecord = ProducerRecord<String, Message<Payload, Schema>>

@Singleton
class KafkaOrderProducer(
    private val configReader: ConfigReader,
    @KafkaClient("order-producer") private val kafkaProducer: Producer<String, Message<Payload, Schema>>
) : OrderProducer {

    private val log = LoggerFactory.getLogger(KafkaOrderProducer::class.java)
    private val objectMapper = ObjectMapper()

    override fun storeOrder(order: Order) {
        val orderMessage = Message(OrderSchema.instance(), order.toPayload())
        val orderItemsMessages = order.items.map { Message(OrderItemSchema.instance(), it.toPayload(order.orderId)) }
        log.info("Sending order message: ${objectMapper.writeValueAsString(orderMessage)}")
        val orderRecord = orderMessage.toRecord(configReader)
        val orderItemsRecords = orderItemsMessages.map { it.toRecord(configReader) }
        kafkaProducer.send(orderRecord)
        orderItemsRecords.forEach{ record -> kafkaProducer.send(record)}
    }

    companion object {
        private fun Order.toPayload(): OrderPayload {
            return OrderPayload(
                id = orderId.value,
                customerId = customerId.value,
                addressId = shippingAddress.value,
                status = status.name
            )
        }
        private fun OrderItem.toPayload(orderId: OrderId): OrderItemPayload {
            return OrderItemPayload(
                orderId = orderId.value,
                productId = productId,
                quantity = quantity
            )
        }

        private fun <T : Payload, S : Schema> Message<T, S>.toRecord(configReader: ConfigReader): CustomRecord {
            return when (schema) {
                is OrderSchema -> ProducerRecord(configReader.ordersTopic,null, this) as CustomRecord
                is OrderItemSchema -> ProducerRecord(configReader.orderItemsTopic, null, this) as CustomRecord
                else -> throw IllegalArgumentException("Unknown schema")
            }
        }
    }
}