package edu.skku.cs.arduinorc_server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ServerApplication

fun main() {
    val gui = GUI()
    runApplication<ServerApplication>()
}

