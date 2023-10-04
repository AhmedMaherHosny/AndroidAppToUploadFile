package com.example.androidapptouploadfile.domain.usecase.upload_use_case

import android.adservices.common.AdTechIdentifier
import com.example.androidapptouploadfile.domain.models.UploadFileDomainModel
import com.example.androidapptouploadfile.domain.repository.RemoteServerRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class UploadFileToServerUseCase @Inject constructor(
    private val remoteServerRepository: RemoteServerRepository
) : UploadUseCase {
    override suspend fun uploadFileToServer(
        contentRange: String,
        fileIdentifier: String,
        file: MultipartBody.Part
    ): UploadFileDomainModel {
        return remoteServerRepository.uploadFileToServer(contentRange, fileIdentifier, file)
    }
}