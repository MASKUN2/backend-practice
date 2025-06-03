package com.example.rediswaitingqueue

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/queue")
class QueueController(
    private val waitingRequestBuffer: WaitingRequestBuffer,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/submit")
    fun submitRequest(@RequestBody waitingRequest: WaitingRequest): ResponseEntity<Response> {
        val userId = waitingRequest.userId
        val accepted = waitingRequestBuffer.offer(Waiting(userId))

        val (status, message) = if (accepted) {
            HttpStatus.ACCEPTED to "userId = $userId 대기열 요청이 수락되었습니다."
        } else {
            log.error("userId = $userId 대기열 요청이 거부되었습니다. 잠시 후 다시 시도하세요.")
            HttpStatus.SERVICE_UNAVAILABLE to "요청을 수락하지 못했습니다. 버퍼가 가득 찼습니다"
        }

        return ResponseEntity.status(status).body(Response(message))
    }
}