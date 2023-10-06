package com.example.androidapptouploadfile.data.local.di

import android.content.Context
import com.example.androidapptouploadfile.data.local.repository.DatastoreRepositoryImpl
import com.example.androidapptouploadfile.domain.repository.local.DatastoreRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().serializeNulls().setLenient().create()
    }

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: Context) = context
}