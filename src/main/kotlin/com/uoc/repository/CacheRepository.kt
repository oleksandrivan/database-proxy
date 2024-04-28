package com.uoc.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.uoc.domain.Order
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

interface CacheRepository {
    fun storeOrder(order: Order)
}

@Singleton
class RedisCacheRepository(
    private val connection: StatefulRedisConnection<String, String>
) : CacheRepository {

    private val objectMapper = ObjectMapper()
    private val logger = LoggerFactory.getLogger(RedisCacheRepository::class.java)

    override fun storeOrder(order: Order) {
        val commands = connection.sync()
        val orderJson = objectMapper.writeValueAsString(order.toEntry())
        commands.set(order.orderId.value, orderJson)
        logger.info("Order stored in cache: ${commands.get(order.orderId.value)}")
    }

    companion object{
        private fun Order.toEntry() = OrderCacheEntry(
            id = orderId.value,
            customerId = customerId.value,
            addressId = shippingAddress.value,
            status = status.name
        )
    }
}
