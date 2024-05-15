package com.uoc.config

import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton

@Singleton
class ConfigReader {

    @Property(name = "kafka.topic.orders.name")
    lateinit var ordersTopic: String

    @Property(name = "kafka.topic.orderItems.name")
    lateinit var orderItemsTopic: String

    @Property(name = "kafka.topic.cacheEviction.name")
    lateinit var cacheEvictionTopic: String

    @Property(name = "database.url")
    lateinit var databaseUrl: String

    @Property(name = "database.username")
    lateinit var username: String

    @Property(name = "database.password")
    lateinit var password: String
}
