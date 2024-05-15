package com.uoc.service

import com.uoc.domain.Order
import com.uoc.kafka.CacheEvictionStream
import com.uoc.repository.CacheRepository
import jakarta.inject.Singleton
import org.slf4j.LoggerFactory

interface CacheEvictionService {

    fun evict(order: Order)
}

@Singleton
class RedisCacheEvictionService(
    private val cacheRepository: CacheRepository
) : CacheEvictionService {

    private val log = LoggerFactory.getLogger(CacheEvictionService::class.java)

    override fun evict(order: Order) {
        log.info("Trying to evict order: {}", order.orderId.value)
        val storedOrder = cacheRepository.getOrder(order.orderId)
        log.info("Retrieved stored record in cache {}", storedOrder)
        storedOrder.onSuccess {
            log.info("Comparing status {} to {}", it.status, order.status)
            log.info("Comparing update timestamp {} to {}", it.updatedAt, order.updatedAt)
            if(isSameEntry(it, order)) {
                log.info("Deleting order {}", order.orderId.value)
                cacheRepository.deleteOrder(order.orderId)
            }
        }
    }

    private fun isSameEntry(storedOrder: Order, orderToEvict: Order): Boolean =
        storedOrder.status == orderToEvict.status && storedOrder.updatedAt == orderToEvict.updatedAt
}
