package com.uoc.repository

import com.uoc.domain.*
import com.uoc.jooq.tables.records.OrderRecord
import com.uoc.jooq.tables.references.ORDER
import jakarta.inject.Singleton
import org.jooq.DSLContext

interface PersistentRepository {
    fun getOrder(orderId: OrderId): Result<Order>
}

@Singleton
class JdbcPersistentRepository(
    private val dslContext: DSLContext
): PersistentRepository {

    override fun getOrder(orderId: OrderId): Result<Order> {
        val order = dslContext.select()
            .from(ORDER)
            .where(ORDER.ID.eq(orderId.value))
            .fetchOne()
        return order?.let {
            Result.success(it.into(OrderRecord::class.java).toDomain())
        } ?: Result.failure(RuntimeException("Order not found"))
    }

    companion object {
        private fun OrderRecord.toDomain(): Order {
            return Order(
                orderId = OrderId(id!!),
                customerId = CustomerId(customerid!!),
                items = listOf(),
                shippingAddress = AddressId(shippingaddress!!),
                status = OrderStatus.valueOf(status!!.name),
                createdAt = createdat!!,
                updatedAt = updatedat!!
            )
        }
    }
}
