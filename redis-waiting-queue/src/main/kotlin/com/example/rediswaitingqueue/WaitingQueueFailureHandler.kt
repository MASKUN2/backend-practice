package com.example.rediswaitingqueue

import com.example.rediswaitingqueue.Waiting.Companion.MAX_RETRY_COUNT
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class WaitingQueueFailureHandler(
    private val waitingRequestBuffer: WaitingRequestBuffer,
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Async
    fun handle(waitingEntries: List<Waiting>) {
        log.info("실패한 대기열 추가 요청을 우선순위 버퍼큐에 넣습니다. 스레드: ${Thread.currentThread().name}")

        val (retriableEntries, overFailedEntries) = waitingEntries
            .map { it.copy(failCount = it.failCount.inc()) }
            .partition { it.failCount < MAX_RETRY_COUNT }


        log.error("총 {}개의 대기 요청이 최대 재시도 횟수({}) 초과로 영구 삭제되었습니다.", overFailedEntries.size, MAX_RETRY_COUNT)
    }
}