package com.example.androidapptouploadfile.domain.models

data class UploadFileDomainModel(
    val message: String,
    val startByte: Int,
    val endByte: Int,
)
