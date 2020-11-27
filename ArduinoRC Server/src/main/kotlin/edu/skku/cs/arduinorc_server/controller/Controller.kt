package edu.skku.cs.arduinorc_server.controller

import edu.skku.cs.arduinorc_server.common.ApiResponse
import edu.skku.cs.arduinorc_server.datatype.PictureData
import edu.skku.cs.arduinorc_server.gui.GUI
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1")
class Controller {
    companion object {
        val gui = GUI()
    }

    @PostMapping("/sendPicture")
    fun getPicture(@RequestBody data: PictureData): ApiResponse {
        val picture = data.picture
        gui.changePicture(picture)

        return ApiResponse.ok("OK")
    }
}
