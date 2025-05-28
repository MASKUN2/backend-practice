package com.example.rediswaitingqueue

import org.springframework.stereotype.Component
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

@Component
class WaitingInfoBuffer {
    private val buffer = LinkedBlockingQueue<WaitingInfo>(100000) // 100K

    /**
     * 지연시간내 처리되지 못하면 false
     */
    fun offer(request: WaitingInfo): Boolean = buffer.offer(request, 500, MILLISECONDS)

    fun poll(le: UInt): List<WaitingInfo> {
        val requests = mutableListOf<WaitingInfo>()
        var count = 1u
        while (count <= le) {
            val request = buffer.poll() ?: break
            requests.add(request)
            count++
        }
        return requests.toList()
    }
}