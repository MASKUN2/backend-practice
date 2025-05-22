package com.example.rediswaitingqueue

import org.springframework.stereotype.Service

@Service
class QueueService (
    private val redisPersistenceScheduler: RedisPersistenceScheduler,
    private val requestBufferQueue: RequestBufferQueue
){

}