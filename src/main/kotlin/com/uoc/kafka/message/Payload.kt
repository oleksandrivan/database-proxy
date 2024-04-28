package com.uoc.kafka.message

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

sealed class Payload

@Serdeable
data class OrderPayload(
    @JsonProperty("id") val id: String,
    @JsonProperty("customerId") val customerId: Int,
    @JsonProperty("shippingAddress") val addressId: Int,
    @JsonProperty("status") val status: String
): Payload()

@Serdeable
data class OrderItemPayload(
    @JsonProperty("orderId") val orderId: String,
    @JsonProperty("productId") val productId: String,
    @JsonProperty("quantity") val quantity: Int
): Payload()
