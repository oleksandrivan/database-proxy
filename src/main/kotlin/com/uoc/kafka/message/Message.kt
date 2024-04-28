package com.uoc.kafka.message

data class Message<T: Payload, S: Schema> (
    val schema: S,
    val payload: T
)
