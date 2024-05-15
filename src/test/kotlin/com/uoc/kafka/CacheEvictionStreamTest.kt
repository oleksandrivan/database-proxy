package com.uoc.kafka

import com.uoc.AbstractIntegrationTest
import com.uoc.repository.CacheRepository
import com.uoc.util.Fixtures.Companion.order
import com.uoc.util.TestKafkaPublisher
import jakarta.inject.Inject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

@Disabled("only for local testing")
class CacheEvictionStreamTest: AbstractIntegrationTest() {

    @Inject
    private lateinit var testKafkaPublisher: TestKafkaPublisher

    @Inject
    private lateinit var cacheRepository: CacheRepository

    @Test
    fun shouldRemoveFromCache(){
        val order = order()
        cacheRepository.storeOrder(order)
        assert(cacheRepository.getOrder(order.orderId).isSuccess)
        testKafkaPublisher.sendTestRecord(order)
        sleep(3_000)
        assert(cacheRepository.getOrder(order.orderId).isFailure)
    }
}
