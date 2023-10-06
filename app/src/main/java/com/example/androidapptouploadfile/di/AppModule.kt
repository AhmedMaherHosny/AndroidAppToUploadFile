package com.example.androidapptouploadfile.di

import com.example.androidapptouploadfile.data.local.repository.DatastoreRepositoryImpl
import com.example.androidapptouploadfile.data.remote.repository.RemoteServerRepositoryImpl
import com.example.androidapptouploadfile.domain.repository.local.DatastoreRepository
import com.example.androidapptouploadfile.domain.repository.remote.RemoteServerRepository
import com.example.androidapptouploadfile.domain.usecase.local_data_store_use_cases.LocalDatastoreUseCasesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindRemoteServerRepository(remoteServerRepositoryImpl: RemoteServerRepositoryImpl): RemoteServerRepository

    @Binds
    @Singleton
    abstract fun bindDatastoreRepository(datastoreRepositoryImpl: DatastoreRepositoryImpl): DatastoreRepository
}