package com.example.rediswaitingqueue

import com.example.rediswaitingqueue.BufferResult.*
import com.example.rediswaitingqueue.WaitingInfo.Companion.MAX_RETRY_COUNT
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class QueueService(
    private val waitingInfoQueue: WaitingInfoQueue,
    private val waitingInfoBuffer: WaitingInfoBuffer,
    private val retryHandler: WaitingInfoRetryHandler,
) {
    companion object {
        private const val CHUNK_SIZE = 1000u // 한 번에 Redis로 저장할 보낼 요청의 수
    }

    private val logger = LoggerFactory.getLogger(javaClass)

    fun addBuffer(waitingInfo: WaitingInfo): BufferResult {
        return if (waitingInfoBuffer.offer(waitingInfo)) Success else BufferFull
    }

    @Scheduled(fixedRateString = "200", timeUnit = TimeUnit.MILLISECONDS)
    private fun persistQueue() {
        val waitingInfos = waitingInfoBuffer.poll(CHUNK_SIZE)

        runCatching {
            waitingInfoQueue.add(waitingInfos)
        }.onFailure { ex ->
            logger.error("Redis에 대기 정보를 추가하지 못했습니다", ex)
            retryHandler.handle(waitingInfos)
        }
    }
}

sealed class BufferResult {
    object Success : BufferResult()
    object BufferFull : BufferResult()
}