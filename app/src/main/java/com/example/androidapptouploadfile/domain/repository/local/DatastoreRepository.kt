package com.example.androidapptouploadfile.domain.repository.local

import com.example.androidapptouploadfile.domain.models.UriDetailsDomainModel

interface DatastoreRepository {
    suspend fun writeUriModelDetailsForUpload(uri:String, value: UriDetailsDomainModel)
    suspend fun readUriModelDetailsForUpload(uri:String) : UriDetailsDomainModel?
    suspend fun deleteUriDetailsAboutUpload(uri:String)
}