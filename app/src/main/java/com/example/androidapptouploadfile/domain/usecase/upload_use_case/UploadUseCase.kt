package com.example.androidapptouploadfile.domain.usecase.upload_use_case

import com.example.androidapptouploadfile.domain.models.UploadFileDomainModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

interface UploadUseCase {
    suspend fun uploadFileToServer(
        contentRange: String,
        file: MultipartBody.Part
    ): UploadFileDomainModel
}