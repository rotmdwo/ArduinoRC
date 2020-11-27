package edu.skku.arduinorc

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServerApi {
    @GET("/api/v1/hello")
    suspend fun hello(): ApiResponse<String>

    @POST("/api/v1/sendPic")
    suspend fun sendPicture(@Body picture: ByteArray): ApiResponse<String>

    companion object {
        val instance = ApiGenerator().generate(ServerApi::class.java)
    }
}