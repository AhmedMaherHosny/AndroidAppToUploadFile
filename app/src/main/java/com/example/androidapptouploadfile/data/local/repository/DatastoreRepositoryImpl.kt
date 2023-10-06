package com.example.androidapptouploadfile.data.local.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidapptouploadfile.data.local.mappers.toUriDetailsDomainModel
import com.example.androidapptouploadfile.data.local.mappers.toUriDetailsLocalModel
import com.example.androidapptouploadfile.data.local.models.UriDetailsLocalModel
import com.example.androidapptouploadfile.domain.models.UriDetailsDomainModel
import com.example.androidapptouploadfile.domain.repository.local.DatastoreRepository
import com.example.androidapptouploadfile.utils.Constants.UPLOAD_PREFERENCES
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = UPLOAD_PREFERENCES)

class DatastoreRepositoryImpl @Inject constructor(
    private val context: Context,
    private val gson: Gson
) : DatastoreRepository {
    override suspend fun writeUriModelDetailsForUpload(uri: String, value: UriDetailsDomainModel) {
        val preferencesKey = stringPreferencesKey(uri)
        val serializedData = gson.toJson(value.toUriDetailsLocalModel())
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = serializedData
        }
    }

    override suspend fun readUriModelDetailsForUpload(uri: String): UriDetailsDomainModel? {
        val preferencesKey = stringPreferencesKey(uri)
        val preferences = context.dataStore.data.first()
        val serializedData = preferences[preferencesKey]
        return if (serializedData != null)
            gson.fromJson(serializedData, UriDetailsLocalModel::class.java)
                .toUriDetailsDomainModel()
        else
            null
    }

    override suspend fun deleteUriDetailsAboutUpload(uri: String) {
        val preferencesKey = stringPreferencesKey(uri)
        context.dataStore.edit { preferences ->
            preferences.remove(preferencesKey)
        }
    }

}