package com.example.rediswaitingqueue

import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

class LogSampler(
    val interval: Duration = Duration.ofSeconds(10),
) {
    private val count = AtomicInteger(0)
    var latestTime : Instant = Instant.now()
        private set

    fun countUp() = count.incrementAndGet()
    fun getCount() = count.get()
    fun isTimeToSample(): Boolean = Instant.now().minus(interval).isAfter(latestTime)
    fun reset() {
        latestTime = Instant.now()
        count.set(0)
    }
}
