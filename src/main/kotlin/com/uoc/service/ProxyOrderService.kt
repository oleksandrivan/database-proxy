package com.uoc.service

import com.uoc.domain.Order
import com.uoc.domain.OrderId
import com.uoc.kafka.OrderProducer
import com.uoc.repository.CacheRepository
import com.uoc.repository.PersistentRepository
import jakarta.inject.Singleton

interface ProxyOrderService {

    fun createOrder(order: Order): Result<OrderId>
    fun getOrder(orderId: OrderId): Result<Order>
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
}
