package com.uoc.kafka.message

data class Message<T, S> (
    val schema: S,
    val payload: T
)
