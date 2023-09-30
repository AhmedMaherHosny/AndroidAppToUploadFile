package com.example.androidapptouploadfile.data.remote.api

import com.example.androidapptouploadfile.data.remote.dtos.UploadFileDto
import okhttp3.MultipartBody
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming

interface RetrofitApi {
    @Streaming
    @Multipart
    @POST("upload/file")
    suspend fun uploadFile(
        @Header("Content-Range") contentRange: String,
        @Part file: MultipartBody.Part
    ): UploadFileDto
}