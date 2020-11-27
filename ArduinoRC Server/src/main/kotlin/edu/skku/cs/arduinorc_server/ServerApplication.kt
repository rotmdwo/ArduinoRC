package edu.skku.cs.arduinorc_server
import edu.skku.cs.arduinorc_server.gui.GUI
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.runApplication

@SpringBootApplication
open class ServerApplication

fun main() {
    System.setProperty("java.awt.headless", "false") // 이거 안 쓰고 컨트롤러에서 gui 실행하면 에러 남
    runApplication<ServerApplication>()
}

