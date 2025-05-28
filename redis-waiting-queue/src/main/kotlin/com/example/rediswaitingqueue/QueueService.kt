package com.example.rediswaitingqueue

import com.example.rediswaitingqueue.QueueResult.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class QueueService (
    private val waitingInfoQueue: WaitingInfoQueue,
    private val waitingInfoBuffer: WaitingInfoBuffer
){
    private val chunkSize = 1000u // 한 번에 Redis로 저장할 보낼 요청의 수

    fun offer(waitingInfo: WaitingInfo): QueueResult {
        return if(waitingInfoBuffer.offer(waitingInfo)) Offered else TimeOut
    }

    @Scheduled(fixedRateString = "200", timeUnit = TimeUnit.MILLISECONDS)
    fun processQueueAndPersistToRedis() {
        val waitingInfos = waitingInfoBuffer.poll(chunkSize)
        if (waitingInfos.isEmpty()) return

        waitingInfoQueue.add(waitingInfos)
    }
}

sealed class QueueResult {
    object Offered : QueueResult()
    object TimeOut : QueueResult()
}