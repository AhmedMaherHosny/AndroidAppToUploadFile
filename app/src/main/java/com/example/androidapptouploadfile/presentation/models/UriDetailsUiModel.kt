package com.example.androidapptouploadfile.presentation.models

data class UriDetailsUiModel(
    val fileIdentifier : String? = null,
    var startByte : Long? = null,
    val progress : Int = 0
)
