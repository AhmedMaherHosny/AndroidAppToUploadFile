package com.example.androidapptouploadfile.presentation.mappers

import com.example.androidapptouploadfile.domain.models.UploadFileDomainModel
import com.example.androidapptouploadfile.presentation.models.UploadFileUiModel

fun UploadFileDomainModel.toUploadFileUiModel(): UploadFileUiModel {
    return UploadFileUiModel(
        message = message,
        startByte = startByte,
        endByte = endByte
    )
}