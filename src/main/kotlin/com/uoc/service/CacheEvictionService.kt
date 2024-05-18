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
        log.info("Trying to evict order: {}", orderToEvict.orderId.value)
        val storedOrder = cacheRepository.getOrder(orderToEvict.orderId)
        log.info("Retrieved stored record in cache {}", storedOrder)
        storedOrder.onSuccess {
            log.info("Comparing status {} to {}", it.status, orderToEvict.status)
            log.info("Comparing update timestamp {} to {}", it.updatedAt, orderToEvict.updatedAt)
            if(isSameEntry(it, orderToEvict)) {
                log.info("Deleting order {}", orderToEvict.orderId.value)
                cacheRepository.deleteOrder(orderToEvict.orderId)
            }
        }
    }

    private fun isSameEntry(storedOrder: Order, orderToEvict: Order): Boolean =
        storedOrder.status == orderToEvict.status && storedOrder.updatedAt == orderToEvict.updatedAt
}
