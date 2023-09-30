package com.example.androidapptouploadfile.data.remote.mappers

import com.example.androidapptouploadfile.data.remote.dtos.UploadFileDto
import com.example.androidapptouploadfile.domain.models.UploadFileDomainModel

fun UploadFileDto.toUploadFileDomainModel(): UploadFileDomainModel {
    return UploadFileDomainModel(
        message = message,
        startByte = startByte,
        endByte = endByte
    )
}