package com.example.androidapptouploadfile.domain.usecase.upload_use_case

import com.example.androidapptouploadfile.domain.models.UploadFileDomainModel
import com.example.androidapptouploadfile.domain.repository.remote.RemoteServerRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadUseCasesImpl @Inject constructor(
    private val remoteServerRepository: RemoteServerRepository
) : UploadUseCases {
    override suspend fun uploadFileToServerUseCase(
        contentRange: String,
        fileIdentifier: String,
        file: MultipartBody.Part
    ): UploadFileDomainModel {
        return remoteServerRepository.uploadFileToServer(contentRange, fileIdentifier, file)
    }
}