package com.uoc.kafka.message

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
    @JsonProperty("registertime") val registerTime: Long,
    @JsonProperty("userid") val userId: String,
    @JsonProperty("regionid") val regionId: String,
    val gender: String
)
