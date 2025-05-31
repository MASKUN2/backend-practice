package com.example.rediswaitingqueue

import com.example.rediswaitingqueue.WaitingInfo.Companion.MAX_RETRY_COUNT
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WaitingInfoRetryHandler(
    private val waitingInfoBuffer: WaitingInfoBuffer,
) {

    private val logger = LoggerFactory.getLogger(javaClass)


    @Async
    fun handle(waitingInfos: List<WaitingInfo>) {
        logger.info("재시도 시작, 스레드: ${Thread.currentThread().name}")
        val retriable = mutableListOf<WaitingInfo>()
        val overFailed = mutableListOf<WaitingInfo>()

        waitingInfos
            .map { it.copy(failCount = it.failCount.inc()) }
            .forEach {
                if (it.failCount < MAX_RETRY_COUNT) {retriable.add(it)} else {overFailed.add(it)}
            }

        retriable.forEach {
            waitingInfoBuffer.offerPrior(it)
        }
        logger.error("총 {}개의 요청이 재시도 횟수 초과로 영구 삭제되었습니다.", overFailed.size)
    }
}