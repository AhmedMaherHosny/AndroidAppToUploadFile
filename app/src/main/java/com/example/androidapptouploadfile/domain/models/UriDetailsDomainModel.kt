package com.example.androidapptouploadfile.domain.models

data class UriDetailsDomainModel(
    val fileIdentifier : String? = null,
    val startByte : Long? = null,
    val progress : Int = 0
)
