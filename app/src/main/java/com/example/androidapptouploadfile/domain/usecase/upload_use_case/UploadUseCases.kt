package com.example.androidapptouploadfile.domain.usecase.upload_use_case

import com.example.androidapptouploadfile.domain.models.UploadFileDomainModel
import okhttp3.MultipartBody

interface UploadUseCases {
    suspend fun uploadFileToServerUseCase(
        contentRange: String,
        fileIdentifier: String,
        file: MultipartBody.Part
    ): UploadFileDomainModel
}