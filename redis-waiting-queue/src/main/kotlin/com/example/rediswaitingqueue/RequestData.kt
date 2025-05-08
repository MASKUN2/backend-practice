package com.example.rediswaitingqueue

import java.time.Instant
import java.util.UUID

data class RequestData(
    val id: String,
    val payload: String,
    val timestamp: Instant = Instant.now()
)