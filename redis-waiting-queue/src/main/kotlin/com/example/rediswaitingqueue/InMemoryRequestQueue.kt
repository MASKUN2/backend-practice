package com.example.rediswaitingqueue

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

@Component
class InMemoryRequestQueue {
    private val queue = ConcurrentLinkedQueue<RequestData>()
    private var lastestQueueSize: AtomicLong = AtomicLong(0)

    fun offer(request: RequestData): Boolean {
        return queue.offer(request)
    }

    fun poll(): RequestData? {
        return queue.poll()
    }

    fun peek(): RequestData? {
        return queue.peek()
    }

    @Scheduled(fixedRate = 100, timeUnit = TimeUnit.MILLISECONDS)
    private fun updateQueueSize() {
        lastestQueueSize.set(queue.size.toLong())
    }

    fun isEmpty(): Boolean {
        return queue.isEmpty() // peek() == null 보다 명확
    }
}