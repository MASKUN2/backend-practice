package com.example.backendpractice

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<BackendPracticeApplication>().with(TestcontainersConfiguration::class).run(*args)
}
