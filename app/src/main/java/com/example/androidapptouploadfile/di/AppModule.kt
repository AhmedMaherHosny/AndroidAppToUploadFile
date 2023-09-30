package com.example.androidapptouploadfile.di

import com.example.androidapptouploadfile.data.remote.repository.RemoteServerRepositoryImpl
import com.example.androidapptouploadfile.domain.repository.RemoteServerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindRemoteServerRepository(remoteServerRepositoryImpl: RemoteServerRepositoryImpl): RemoteServerRepository
}