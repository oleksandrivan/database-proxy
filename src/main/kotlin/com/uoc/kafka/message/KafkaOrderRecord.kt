package com.uoc.kafka.message

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class KafkaOrderRecord(
    @JsonProperty("id") val id: String,
    @JsonProperty("customerId") val customerId: Int,
    @JsonProperty("shippingAddress") val addressId: Int,
    @JsonProperty("status") val status: String,
    @JsonProperty("createdAt") val createdAt: Long,
    @JsonProperty("updatedAt") val updatedAt: Long
)
