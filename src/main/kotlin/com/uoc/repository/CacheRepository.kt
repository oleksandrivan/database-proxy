package com.uoc.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.uoc.domain.*
import io.lettuce.core.api.StatefulRedisConnection
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

interface CacheRepository {
    fun storeOrder(order: Order)
    fun getOrder(orderId: OrderId): Result<Order>
}

@Singleton
class RedisCacheRepository(
    private val connection: StatefulRedisConnection<String, String>
) : CacheRepository {

    private val objectMapper = ObjectMapper()

    override fun storeOrder(order: Order) {
        val commands = connection.sync()
        val orderJson = objectMapper.writeValueAsString(order.toEntry())
        commands.set(order.orderId.value, orderJson)

    }

    override fun getOrder(orderId: OrderId): Result<Order> {
        val commands = connection.sync()
        val orderJson: String? = commands.get(orderId.value)
        return orderJson?.let{
            val entry = objectMapper.readValue(it, OrderCacheEntry::class.java)
            Result.success(entry.toDomain())
        } ?: Result.failure(RuntimeException("Order not found"))
    }

    companion object{
        private fun Order.toEntry() = OrderCacheEntry(
            id = orderId.value,
            customerId = customerId.value,
            addressId = shippingAddress.value,
            status = status.name
        )

        private fun OrderCacheEntry.toDomain() = Order(
            orderId = OrderId(id),
            customerId = CustomerId(customerId),
            items = emptyList(),
            shippingAddress = AddressId(addressId),
            status = OrderStatus.valueOf(status)
        )
    }
}
