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
class QueueController(private val inMemoryRequestQueue: InMemoryRequestQueue) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/submit")
    fun submitRequest(@RequestBody requestData: RequestData): ResponseEntity<String> {

        val success = inMemoryRequestQueue.offer(requestData)

        return if (success) {
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Request ${requestData.id} accepted.")
        } else {
            logger.warn("Failed to submit request to in-memory queue. Queue might be full or unavailable.")
            ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Failed to accept request.")
        }
    }
}