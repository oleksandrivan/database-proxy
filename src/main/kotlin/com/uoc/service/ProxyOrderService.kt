package com.uoc.service

import com.uoc.domain.Order
import com.uoc.domain.OrderId
import com.uoc.domain.OrderStatus
import com.uoc.kafka.OrderProducer
import com.uoc.repository.CacheRepository
import com.uoc.repository.PersistentRepository
import jakarta.inject.Singleton

interface ProxyOrderService {

    fun createOrder(order: Order): Result<OrderId>
    fun getOrder(orderId: OrderId): Result<Order>
    fun updateOrder(orderId: OrderId, orderStatus: OrderStatus): Result<OrderId>
}

@Singleton
class ProxyOrderServiceImpl(
    private val cacheRepository: CacheRepository,
    private val orderProducer: OrderProducer,
    private val persistentRepository: PersistentRepository
): ProxyOrderService {

    override fun createOrder(order: Order): Result<OrderId> = kotlin.runCatching {
        cacheRepository.storeOrder(order)
        orderProducer.storeOrder(order)
        order.orderId
    }

    override fun getOrder(orderId: OrderId): Result<Order> {
        val cachedOrder = cacheRepository.getOrder(orderId)
        return if (cachedOrder.isSuccess) {
            cachedOrder
        } else {
            persistentRepository.getOrder(orderId)
        }
    }

    override fun updateOrder(orderId: OrderId, orderStatus: OrderStatus): Result<OrderId> {
        val order = getOrder(orderId)
        return order.map {
            val updatedOrder = it.copy(status = orderStatus)
            cacheRepository.storeOrder(updatedOrder)
            orderProducer.storeOrder(updatedOrder)
            updatedOrder.orderId
        }
    }
}
