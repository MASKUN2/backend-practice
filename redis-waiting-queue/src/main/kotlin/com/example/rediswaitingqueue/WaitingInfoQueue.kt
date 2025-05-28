package com.example.rediswaitingqueue

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisZSetCommands
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class WaitingInfoQueue(
    private val stringRedisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper, // JSON 문자열로 저장하므로 StringRedisTemplate 사용
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val redisQueueKey = "waiting:queue"

    fun add(waitingInfos: List<WaitingInfo>) {
        if (waitingInfos.isEmpty()) return

        stringRedisTemplate.executePipelined {
            waitingInfos.forEach(zAddNx(it))
            return@executePipelined null
        }
    }

    private fun zAddNx(connection: RedisConnection): (WaitingInfo) -> Unit = {
        connection.zSetCommands().zAdd(
            redisQueueKey.toByteArray(),
            it.waitingRequestAt.toEpochMilli().toDouble(),
            it.userId.toByteArray(),
            RedisZSetCommands.ZAddArgs.ifNotExists()
        )
    }
}