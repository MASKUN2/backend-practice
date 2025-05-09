package com.example.rediswaitingqueue

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RedisPersistenceScheduler(
    private val inMemoryRequestQueue: InMemoryRequestQueue,
    private val stringRedisTemplate: StringRedisTemplate, // JSON 문자열로 저장하므로 StringRedisTemplate 사용
    private val objectMapper: ObjectMapper, // 객체를 JSON 문자열로 변환
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val redisQueueKey = "requests:pending" // Redis에 저장될 List의 키
    private val chunkSize = 1000 // 한 번에 Redis로 보낼 요청의 수 (튜닝 필요)

    @Scheduled(fixedRateString = "\${queue.processing.interval:100}") // 100ms 마다 실행, application.properties에서 설정 가능
    fun processQueueAndPersistToRedis() {
        val requestsToProcess = mutableListOf<RequestData>()
        var count = 0
        while (count < chunkSize) {
            val request = inMemoryRequestQueue.poll() ?: break // 큐가 비었으면 루프 종료
            requestsToProcess.add(request)
            count++
        }
        if (requestsToProcess.isNotEmpty()) {
            return
        }

        try {
            logger.debug("Processing batch of {} requests from in-memory queue.", requestsToProcess.size)

            // Redis Pipelining: 여러 명령을 한 번의 네트워크 요청으로 보냄
            stringRedisTemplate.executePipelined { connection ->
                requestsToProcess.forEach { requestData ->
                    try {
                        val jsonRequest = objectMapper.writeValueAsString(requestData)
                        // RPUSH: 리스트의 오른쪽에 요소 추가
                        connection.listCommands().rPush(redisQueueKey.toByteArray(), jsonRequest.toByteArray())
                    } catch (e: Exception) {
                        logger.error("Failed to serialize request data: ${requestData.id}", e)
                        // 직렬화 실패 시 해당 요청은 누락될 수 있음. 에러 처리 전략 필요.
                    }
                }
            }

            logger.info(
                "Successfully persisted {} requests to Redis list '{}'",
                requestsToProcess.size, redisQueueKey
            )
        } catch (e: Exception) {
            logger.error("Error persisting requests to Redis: {}", e.message, e)
            // 실패한 요청들 재처리 로직 (중요):
            // 1. 다시 인메모리 큐의 앞부분에 넣기 (순서 보장 시, 단 무한 반복 주의)
            //    requestsToProcess.asReversed().forEach { inMemoryRequestQueue.offerFirst(it) } -> ConcurrentLinkedQueue는 offerFirst 없음. 별도 Deque 구현 필요.
            //    또는 다시 offer (순서 변경 가능성)
            //    requestsToProcess.forEach { inMemoryRequestQueue.offer(it) }
            // 2. 별도의 실패 큐/로그/DB에 저장 후 나중에 재처리
            // 3. 요청 버리기 (데이터 유실)
            // 여기서는 간단히 로그만 남기고 요청은 유실됩니다. 실제 환경에서는 재처리 방안이 필수입니다.
            logger.warn("Failed requests are currently not re-queued. Batch size: {}", requestsToProcess.size)
        }
    }
}