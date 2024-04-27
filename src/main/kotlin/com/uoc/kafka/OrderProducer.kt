package com.uoc.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.uoc.config.ConfigReader
import com.uoc.domain.Order
import com.uoc.kafka.message.Message
import com.uoc.kafka.message.User
import com.uoc.kafka.message.UserSchema
import io.micronaut.configuration.kafka.annotation.KafkaClient
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory

interface OrderProducer {
    fun storeOrder(order: Order)
    fun produceUserMessage(user: User)
}

@Singleton
class KafkaOrderProducer(
    private val configReader: ConfigReader,
) : OrderProducer {

    @Inject
    @KafkaClient("user-producer")
    private lateinit var kafkaProducer: Producer<String, Message<User, UserSchema>>

    private val log = LoggerFactory.getLogger(KafkaOrderProducer::class.java)
    private val objectMapper = ObjectMapper()

    override fun storeOrder(order: Order) {
        log.info("Storing order: $order")
    }

    override fun produceUserMessage(user: User) {
        val message = Message(UserSchema.instance(), user)
        log.info("Sending user message: ${objectMapper.writeValueAsString(message)}")
        val record = ProducerRecord<String, Message<User, UserSchema>>("customers", message)
        kafkaProducer.send(record)
    }
}