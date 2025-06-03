package com.example.rediswaitingqueue

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class QueueScheduler(
    private val waitingQueue: WaitingQueue,
    private val requestBuffer: WaitingRequestBuffer,
    private val failureHandler: WaitingQueueFailureHandler,
) {
    companion object {
        private const val CHUNK_SIZE = 1000u // 한 번에 Redis로 저장할 보낼 요청의 수
    }
    private final val log = LoggerFactory.getLogger(javaClass)
    private final val logSampler = LogSampler()

    @Scheduled(fixedRateString = "200", timeUnit = TimeUnit.MILLISECONDS)
    private fun persistQueue() {
        val waitingInfos = requestBuffer.poll(CHUNK_SIZE)

        runCatching {
            waitingQueue.add(waitingInfos)
        }.onFailure { ex ->
            if (logSampler.isTimeToSample()) {
                log.error("예외 발생", ex)
                log.error("Redis에 대기 정보를 추가하지 못했습니다. 마지막 로그({}) 사이에 {}번의 예외가 발생했습니다.",logSampler.latestTime, logSampler.getCount())
            }else{
                logSampler.countUp()
            }
            failureHandler.handle(waitingInfos)
        }
    }
}

