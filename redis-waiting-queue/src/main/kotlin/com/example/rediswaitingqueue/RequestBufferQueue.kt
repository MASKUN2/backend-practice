package com.example.rediswaitingqueue

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicLong

@Component
class RequestBufferQueue {
    private val queue = ConcurrentLinkedQueue<QueueRequest>()
    private var lastestQueueSize: AtomicLong = AtomicLong(0)

    fun offer(request: QueueRequest): Boolean = queue.offer(request)

    fun poll(): QueueRequest? = queue.poll()

    fun poll(le: UInt): List<QueueRequest> {
        val requests = mutableListOf<QueueRequest>()
        var count = 0u
        while (count <= le) {
            val request = queue.poll() ?: break
            requests.add(request)
            count++
        }
        return requests.toList()
    }
}