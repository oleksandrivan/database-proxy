package com.uoc.kafka

import com.github.rholder.retry.RetryerBuilder
import com.github.rholder.retry.StopStrategies
import com.github.rholder.retry.WaitStrategies
import com.google.common.base.Predicates
import com.uoc.domain.Order
import com.uoc.repository.CacheRepository
import com.uoc.util.Fixtures.Companion.order
import com.uoc.util.TestKafkaPublisher
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

@MicronautTest
@Disabled("Does not work in CI")
class CacheEvictionStreamTest {

    @Inject
    private lateinit var testKafkaPublisher: TestKafkaPublisher

    @Inject
    private lateinit var cacheRepository: CacheRepository

    @Test
    fun shouldRemoveFromCache() {
        val order = order()
        cacheRepository.storeOrder(order)
        assert(cacheRepository.getOrder(order.orderId).isSuccess)
        testKafkaPublisher.sendTestRecord(order)
        sleep(7_000)
        val callable: Callable<Result<Order>> = Callable {
            val orderResult = cacheRepository.getOrder(order.orderId)
            if (orderResult.isFailure) {
                orderResult
            } else {
                throw RuntimeException()
            }
        }
        val retryer = RetryerBuilder.newBuilder<Result<Order>>()
            .retryIfExceptionOfType(RuntimeException::class.java)
            .withStopStrategy(StopStrategies.stopAfterAttempt(7))
            .withWaitStrategy(WaitStrategies.fixedWait(1L, TimeUnit.SECONDS))
            .build()
        val callableResult = retryer.call(callable)
        assert(callableResult.isFailure)
    }
}
