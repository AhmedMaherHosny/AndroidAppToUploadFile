package com.example.androidapptouploadfile.domain.usecase.local_data_store_use_cases

import com.example.androidapptouploadfile.data.local.repository.DatastoreRepositoryImpl
import com.example.androidapptouploadfile.domain.models.UriDetailsDomainModel
import com.example.androidapptouploadfile.domain.repository.local.DatastoreRepository
import javax.inject.Inject

class LocalDatastoreUseCasesImpl @Inject constructor(
    private val datastoreRepository: DatastoreRepository
) : LocalDatastoreUseCases {
    override suspend fun writeUriModelDetailsForUploadUseCase(
        uri: String,
        value: UriDetailsDomainModel
    ) {
        datastoreRepository.writeUriModelDetailsForUpload(uri, value)
    }

    override suspend fun readUriModelDetailsForUploadUseCase(uri: String): UriDetailsDomainModel? =
        datastoreRepository.readUriModelDetailsForUpload(uri)


    override suspend fun deleteUriDetailsAboutUploadUseCase(uri: String) {
        datastoreRepository.deleteUriDetailsAboutUpload(uri)
    }

}