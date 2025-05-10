package com.example.rediswaitingqueue

import java.time.Instant

data class RequestData(
    val id: String,
    val timestamp: Instant = Instant.now()
)