package com.example.rediswaitingqueue

import com.example.rediswaitingqueue.QueueResult.Offered
import com.example.rediswaitingqueue.QueueResult.TimeOut
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
    private val waitingInfoBuffer: WaitingInfoBuffer,
    private val queueService: QueueService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/submit")
    fun submitRequest(@RequestBody waitingRequest: WaitingRequest): ResponseEntity<Response> {
        val userId = waitingRequest.userId
        val queueResult: QueueResult = queueService.offer(WaitingInfo(userId))
        return when (queueResult) {
            Offered -> ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Response("userId=$userId accepted."))

            TimeOut -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Response("Failed to accept request."))
        }
    }
}