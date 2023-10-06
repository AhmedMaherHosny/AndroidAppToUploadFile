package com.example.androidapptouploadfile.presentation.di

import com.example.androidapptouploadfile.domain.usecase.local_data_store_use_cases.LocalDatastoreUseCases
import com.example.androidapptouploadfile.domain.usecase.local_data_store_use_cases.LocalDatastoreUseCasesImpl
import com.example.androidapptouploadfile.domain.usecase.upload_use_case.UploadUseCasesImpl
import com.example.androidapptouploadfile.domain.usecase.upload_use_case.UploadUseCases
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
abstract class ServiceModule {
    @Binds
    @ServiceScoped
    abstract fun bindUploadUseCase(uploadUseCasesImpl: UploadUseCasesImpl): UploadUseCases

    @Binds
    @ServiceScoped
    abstract fun bindDatastoreUseCase(localDatastoreUseCasesImpl: LocalDatastoreUseCasesImpl): LocalDatastoreUseCases
}
