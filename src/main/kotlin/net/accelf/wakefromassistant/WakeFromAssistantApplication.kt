package net.accelf.wakefromassistant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WakeFromAssistantApplication

fun main(args: Array<String>) {
    runApplication<WakeFromAssistantApplication>(*args)
}
