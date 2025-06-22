package tobyspring.splearn

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SplearnApplication

@Suppress("SpreadOperator") // ignore detekt
fun main(args: Array<String>) {
    runApplication<SplearnApplication>(*args)
}
