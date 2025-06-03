package com.example.rediswaitingqueue

import org.springframework.stereotype.Component
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit.*

@Component
class WaitingRequestBuffer {
    /**
     * 기본 큐
     */
    private val mainQueue = LinkedBlockingQueue<Waiting>(100_000)
    /**
     * 우선순위 큐
     */
    private val priorQueue = LinkedBlockingQueue<Waiting>(10_000)

    fun offer(request: Waiting): Boolean = mainQueue.offer(request, 200, MILLISECONDS)
    fun offerPrior(request: Waiting) = priorQueue.offer(request, 200, MILLISECONDS)

    /**
     * 우선 순위 버퍼를 먼저 가져옵니다.
     */
    fun poll(le: UInt): List<Waiting> {
        val requests = mutableListOf<Waiting>()
        var count = 1u
        while (count <= le) {
            val request = priorQueue.poll() ?: mainQueue.poll() ?: break
            requests.add(request)
            count++
        }
        return requests.toList()
    }
}