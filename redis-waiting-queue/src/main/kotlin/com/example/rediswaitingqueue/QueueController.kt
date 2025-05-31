package com.example.rediswaitingqueue

import com.example.rediswaitingqueue.BufferResult.Success
import com.example.rediswaitingqueue.BufferResult.BufferFull
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
    private val waitingInfoRetryHandler: WaitingInfoRetryHandler,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/submit")
    fun submitRequest(@RequestBody waitingRequest: WaitingRequest): ResponseEntity<Response> {
        val userId = waitingRequest.userId
        val result: BufferResult = waitingInfoRetryHandler.addBuffer(WaitingInfo(userId))
        return when (result) {
            Success -> ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Response("userId = $userId 대기열 요청이 수락되었습니다."))

            BufferFull -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Response("요청을 수락하지 못했습니다. 버퍼가 가득 찼습니다"))
        }
    }
}