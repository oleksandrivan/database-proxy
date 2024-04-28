package com.uoc.repository

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderCacheEntry(
    @JsonProperty("id") val id: String,
    @JsonProperty("customerId") val customerId: Int,
    @JsonProperty("shippingAddress") val addressId: Int,
    @JsonProperty("status") val status: String
)
