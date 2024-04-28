package com.uoc.repository

import com.uoc.util.Fixtures.Companion.order
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.Test

@MicronautTest
class RedisCacheRepositoryTest {

    @Inject
    lateinit var redisCacheRepository: RedisCacheRepository

    @Test
    fun storeOrder() {
        val order = order()
        redisCacheRepository.storeOrder(order)
    }
}