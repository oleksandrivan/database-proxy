package com.uoc.kafka.message

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class Message<T: Payload, S: Schema> (
    val schema: S,
    val payload: T
)
