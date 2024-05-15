package com.uoc.repository

import com.uoc.AbstractIntegrationTest
import com.uoc.domain.OrderStatus
import com.uoc.util.Fixtures.Companion.order
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled

import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@Disabled("Only for local testing")
class RedisCacheRepositoryTest : AbstractIntegrationTest() {

    @Inject
    lateinit var redisCacheRepository: RedisCacheRepository

    @Test
    fun storeUpdateAndDeleteOrder() {
        val order = order()
        redisCacheRepository.storeOrder(order)
        val storedOrder = redisCacheRepository.getOrder(order.orderId)
        storedOrder.fold(
            onSuccess = {
                assert(order.customerId == it.customerId)
                assert(order.shippingAddress == it.shippingAddress)
                assert(order.createdAt == it.createdAt)
                assert(order.updatedAt == it.updatedAt)
            },
            onFailure = { fail("Order not found") }
        )
        val updatedTime = LocalDateTime.of(2025, 1, 1, 14, 0, 1)
        val updatedOrder = order.copy(status = OrderStatus.DELIVERED, updatedAt = updatedTime)
        redisCacheRepository.storeOrder(updatedOrder)
        val updatedStoredOrder = redisCacheRepository.getOrder(order.orderId)
        updatedStoredOrder.fold(
            onSuccess = {
                assert(updatedOrder.updatedAt == updatedTime)
            },
            onFailure = { fail("Order not found") }
        )
        val deleteResult = redisCacheRepository.deleteOrder(order.orderId)
        assertTrue(deleteResult.isSuccess)
        val deletedOrder = redisCacheRepository.getOrder(order.orderId)
        assertTrue(deletedOrder.isFailure)
    }
}
