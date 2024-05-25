package com.uoc.service

import com.uoc.domain.Order
import com.uoc.repository.CacheRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

interface CacheEvictionService {

    fun evict(orderToEvict: Order)
}

@Singleton
class RedisCacheEvictionService(
    private val cacheRepository: CacheRepository
) : CacheEvictionService {

    private val log = LoggerFactory.getLogger(CacheEvictionService::class.java)

    override fun evict(orderToEvict: Order) {
        val storedOrder = cacheRepository.getOrder(orderToEvict.orderId)
        storedOrder.onSuccess {
            if(isSameEntry(it, orderToEvict)) {
                log.info("Deleting order {}", orderToEvict.orderId.value)
                cacheRepository.deleteOrder(orderToEvict.orderId)
            }
        }
    }

    private fun isSameEntry(storedOrder: Order, orderToEvict: Order): Boolean =
        storedOrder.status == orderToEvict.status && storedOrder.updatedAt == orderToEvict.updatedAt
}
