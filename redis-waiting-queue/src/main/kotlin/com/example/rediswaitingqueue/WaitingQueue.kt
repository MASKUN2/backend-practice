package com.example.rediswaitingqueue

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Component
class WaitingQueue(
    private val stringRedisTemplate: StringRedisTemplate,
) {

    companion object {
        private const val QUEUE_KEY = "waiting:queue"
    }

    fun add(waitings: List<Waiting>) {
        if (waitings.isEmpty()) return
        stringRedisTemplate.opsForZSet().addIfAbsent(QUEUE_KEY, waitings.asTuplesSet())
    }

    /**
     * 대기열에 등록하기 편한 형태로 변경해주는 확장함수
     */
    fun List<Waiting>.asTuplesSet(): Set<ZSetOperations.TypedTuple<String>> =
        this.map {
            ZSetOperations.TypedTuple.of(
                it.userId, // userId를 중복되지 않는 Value로 지정됩니다.
                it.waitingRequestAt.toEpochMilli().toDouble()
            )
        }.toSet()
}