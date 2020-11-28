package edu.skku.cs.arduinorc_server.controller

import edu.skku.cs.arduinorc_server.common.ApiResponse
import edu.skku.cs.arduinorc_server.datatype.PictureData
import edu.skku.cs.arduinorc_server.gui.GUI
import org.springframework.web.bind.annotation.*
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.util.*

@RestController
@RequestMapping("/api/v1")
class Controller {
    companion object {
        val gui = GUI()
        val buffer = LinkedList<Char>()
    }

    @PostMapping("/sendPicture")
    fun getPicture(@RequestBody data: PictureData): ApiResponse {
        val picture = data.picture
        gui.changePicture(picture)

        val stringBuilder = StringBuilder()
        while (buffer.isNotEmpty()) stringBuilder.append(buffer.poll())

        return ApiResponse.ok(stringBuilder.toString())
    }
}
