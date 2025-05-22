package com.example.rediswaitingqueue

import java.time.Instant

data class QueueData(
    val id: String,
    val timestamp: Instant = Instant.now()
)