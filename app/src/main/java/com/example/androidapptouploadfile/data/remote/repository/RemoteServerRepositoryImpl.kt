package com.example.androidapptouploadfile.data.remote.repository

import com.example.androidapptouploadfile.data.remote.api.RetrofitApi
import com.example.androidapptouploadfile.data.remote.mappers.toUploadFileDomainModel
import com.example.androidapptouploadfile.domain.models.UploadFileDomainModel
import com.example.androidapptouploadfile.domain.repository.RemoteServerRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class RemoteServerRepositoryImpl @Inject constructor(
    private val retrofitApi: RetrofitApi
) : RemoteServerRepository {
    override suspend fun uploadFileToServer(
        contentRange: String,
        fileIdentifier: String,
        file: MultipartBody.Part
    ): UploadFileDomainModel {
        return retrofitApi.uploadFile(contentRange, fileIdentifier, file).toUploadFileDomainModel()
    }
}