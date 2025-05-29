package com.example.rediswaitingqueue

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Component
class WaitingInfoQueue(
    private val stringRedisTemplate: StringRedisTemplate,
) {

    companion object {
        private const val QUEUE_KEY = "waiting:queue"
    }

    /**
     * 이미 등록된 user는 대기열에 등록하지 않습니다.
     */
    fun add(waitingInfos: List<WaitingInfo>) {
        stringRedisTemplate.opsForZSet().addIfAbsent(QUEUE_KEY, waitingInfos.asTuplesSet())
    }

    /**
     * 대기열에 등록하기 편한 형태로 변경해주는 확장함수
     */
    fun List<WaitingInfo>.asTuplesSet(): Set<ZSetOperations.TypedTuple<String>> =
        this.map { ZSetOperations.TypedTuple.of(it.userId, it.waitingRequestAt.toEpochMilli().toDouble()) }.toSet()
}