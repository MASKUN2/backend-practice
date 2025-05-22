package com.example.rediswaitingqueue

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisZSetCommands
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisPersistenceScheduler(
    private val requestBufferQueue: RequestBufferQueue,
    private val stringRedisTemplate: StringRedisTemplate, // JSON 문자열로 저장하므로 StringRedisTemplate 사용
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val redisQueueKey = "requests:queue"
    private val chunkSize = 1000u // 한 번에 Redis로 보낼 요청의 수 (튜닝 필요)

    @Scheduled(fixedRateString = "200", timeUnit = TimeUnit.MILLISECONDS)
    fun processQueueAndPersistToRedis() {
        val requests = requestBufferQueue.poll(chunkSize)
        if (requests.isEmpty()) return

        stringRedisTemplate.executePipelined {
            requests.forEach(persist(it))
            return@executePipelined null
        }
    }

    private fun persist(connection: RedisConnection): (QueueRequest) -> Unit = { request ->
        connection.zSetCommands().zAdd(
            redisQueueKey.toByteArray(),
            request.registeredAt.toEpochMilli().toDouble(),
            request.id.toByteArray(),
            RedisZSetCommands.ZAddArgs.ifNotExists()
        )
    }
}