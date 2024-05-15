package com.uoc.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.uoc.domain.*
import io.lettuce.core.api.StatefulRedisConnection
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.time.ZoneOffset

interface CacheRepository {
    fun storeOrder(order: Order)
    fun getOrder(orderId: OrderId): Result<Order>
    fun deleteOrder(orderId: OrderId): Result<Unit>
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

    override fun deleteOrder(orderId: OrderId): Result<Unit> {
        val commands = connection.sync()
        return if (commands.del(orderId.value) == 1L) {
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("Order not found"))
        }
    }

    companion object{
        private fun Order.toEntry() = OrderCacheEntry(
            id = orderId.value,
            customerId = customerId.value,
            addressId = shippingAddress.value,
            status = status.name,
            createdAt = createdAt.toEpochSecond(ZoneOffset.UTC),
            updatedAt = updatedAt.toEpochSecond(ZoneOffset.UTC),
        )

        private fun OrderCacheEntry.toDomain() = Order(
            orderId = OrderId(id),
            customerId = CustomerId(customerId),
            shippingAddress = AddressId(addressId),
            status = OrderStatus.valueOf(status),
            createdAt = LocalDateTime.ofEpochSecond(createdAt, 0, ZoneOffset.UTC),
            updatedAt = LocalDateTime.ofEpochSecond(updatedAt, 0, ZoneOffset.UTC),
        )
    }
}
