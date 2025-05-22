package com.example.rediswaitingqueue

import java.time.Instant
import java.util.UUID

data class QueueRequest(
    val id: String,
    val registeredAt: Instant,
    val failCount: Int = 0
)