package com.example.androidapptouploadfile.presentation.mappers

import com.example.androidapptouploadfile.data.local.models.UriDetailsLocalModel
import com.example.androidapptouploadfile.domain.models.UriDetailsDomainModel
import com.example.androidapptouploadfile.presentation.models.UriDetailsUiModel

fun UriDetailsUiModel.toUriDetailsDomainModel(): UriDetailsDomainModel =
    UriDetailsDomainModel(
        fileIdentifier = fileIdentifier,
        startByte = startByte,
        progress = progress
    )