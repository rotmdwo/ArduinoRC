package edu.skku.cs.arduinorc_server.controller

import edu.skku.cs.arduinorc_server.common.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class Controller {
    @PostMapping("/sendPicture")
    fun getPicture(@RequestBody picture: ByteArray): ApiResponse {
        return ApiResponse.ok("OK")
    }
}
