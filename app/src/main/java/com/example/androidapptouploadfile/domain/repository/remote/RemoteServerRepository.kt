package com.example.androidapptouploadfile.domain.repository.remote

import com.example.androidapptouploadfile.domain.models.UploadFileDomainModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

interface RemoteServerRepository {
    suspend fun uploadFileToServer(
        contentRange: String,
        fileIdentifier: String,
        file: MultipartBody.Part
    ): UploadFileDomainModel
}