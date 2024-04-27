package com.uoc.kafka.message

import com.fasterxml.jackson.annotation.JsonProperty

data class Field(
    @JsonProperty("field") val fieldName: String,
    @JsonProperty("type") val fieldType: String,
    val optional: Boolean
)
