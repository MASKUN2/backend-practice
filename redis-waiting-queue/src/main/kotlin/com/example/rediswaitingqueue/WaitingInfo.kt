package com.example.rediswaitingqueue

import java.time.Instant
import java.util.UUID



data class WaitingInfo(
    val userId: String,
    val waitingRequestAt: Instant = Instant.now(),
    val failCount: UInt = 0u,
){
    companion object{
        val MAX_RETRY_COUNT = 3u
    }
}