package com.uoc.kafka.message

import io.micronaut.serde.annotation.Serdeable

sealed class Schema {
    abstract val type: String
    abstract val fields: List<Field>
}

@Serdeable
data class OrderSchema(
    override val type: String = "struct",
    override val fields: List<Field>
): Schema() {
    companion object {
        fun instance(): OrderSchema {
            val orderId = Field("id", "string", false)
            val customerId = Field("customerId", "int64", false)
            val addressId = Field("shippingAddress", "int64", false)
            val status = Field("status", "string", false)
            return OrderSchema(fields = listOf(orderId, customerId, addressId, status))
        }
    }
}

@Serdeable
data class OrderItemSchema(
    override val type: String = "struct",
    override val fields: List<Field>
): Schema() {
    companion object {
        fun instance(): OrderItemSchema {
            val orderId = Field("orderId", "string", false)
            val productId = Field("productId", "string", false)
            val quantity = Field("quantity", "int32", false)
            return OrderItemSchema(fields = listOf(orderId, productId, quantity))
        }
    }
}
