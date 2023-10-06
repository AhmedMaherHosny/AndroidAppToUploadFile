package com.example.androidapptouploadfile.domain.usecase.local_data_store_use_cases

import com.example.androidapptouploadfile.domain.models.UriDetailsDomainModel

interface LocalDatastoreUseCases {
    suspend fun writeUriModelDetailsForUploadUseCase(uri:String, value: UriDetailsDomainModel)
    suspend fun readUriModelDetailsForUploadUseCase(uri:String) : UriDetailsDomainModel?
    suspend fun deleteUriDetailsAboutUploadUseCase(uri:String)
}