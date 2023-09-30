package com.example.androidapptouploadfile.data.remote.dtos


import com.google.gson.annotations.SerializedName

data class UploadFileDto(
    @SerializedName("endByte")
    val endByte: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("startByte")
    val startByte: Int
)