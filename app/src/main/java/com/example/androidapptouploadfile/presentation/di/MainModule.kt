package com.example.androidapptouploadfile.presentation.di

import android.content.ContentResolver
import android.content.Context
import com.example.androidapptouploadfile.data.remote.repository.RemoteServerRepositoryImpl
import com.example.androidapptouploadfile.domain.repository.RemoteServerRepository
import com.example.androidapptouploadfile.domain.usecase.upload_use_case.UploadFileToServerUseCase
import com.example.androidapptouploadfile.domain.usecase.upload_use_case.UploadUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceModule {
    @Binds
    @ServiceScoped
    abstract fun bindUploadUseCase(uploadFileToServerUseCase: UploadFileToServerUseCase): UploadUseCase
}
