package com.uoc.config

import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton

@Singleton
class ConfigReader {

    @Property(name = "kafka.topic.orders.name")
    lateinit var ordersTopic: String

    @Property(name = "kafka.topic.orderItems.name")
    lateinit var orderItemsTopic: String
}
