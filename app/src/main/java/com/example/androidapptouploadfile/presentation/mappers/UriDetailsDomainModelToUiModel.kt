package com.example.androidapptouploadfile.presentation.mappers

import com.example.androidapptouploadfile.data.local.models.UriDetailsLocalModel
import com.example.androidapptouploadfile.domain.models.UriDetailsDomainModel
import com.example.androidapptouploadfile.presentation.models.UriDetailsUiModel

fun UriDetailsDomainModel.toUriDetailsUiModel(): UriDetailsUiModel =
    UriDetailsUiModel(
        fileIdentifier = fileIdentifier,
        startByte = startByte,
        progress = progress
    )