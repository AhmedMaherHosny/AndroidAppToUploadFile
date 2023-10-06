package com.example.androidapptouploadfile.data.local.mappers

import com.example.androidapptouploadfile.data.local.models.UriDetailsLocalModel
import com.example.androidapptouploadfile.domain.models.UriDetailsDomainModel

fun UriDetailsLocalModel.toUriDetailsDomainModel(): UriDetailsDomainModel =
    UriDetailsDomainModel(
        fileIdentifier = fileIdentifier,
        startByte = startByte,
        progress = progress
    )