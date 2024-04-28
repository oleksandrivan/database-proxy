package com.uoc.kafka.message

import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class Field(
    @JsonProperty("field") val fieldName: String,
    @JsonProperty("type") val fieldType: String,
    val optional: Boolean
)
