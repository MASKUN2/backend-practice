package com.example.rediswaitingqueue

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<RedisWaitingQueueApplication>().with(TestcontainersConfiguration::class).run(*args)
}
