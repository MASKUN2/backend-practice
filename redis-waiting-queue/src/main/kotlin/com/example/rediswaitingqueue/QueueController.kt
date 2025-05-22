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
class QueueController(private val requestBufferQueue: RequestBufferQueue) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/submit")
    fun submitRequest(@RequestBody queueRequest: QueueRequest): ResponseEntity<String> {

        val success = requestBufferQueue.offer(queueRequest)

        return if (success) {
            ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body("Request ${queueRequest.id} accepted.")
        } else {
            logger.warn("Failed to submit request to in-memory queue. Queue might be full or unavailable.")
            ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Failed to accept request.")
        }
    }
}