package com.rswiftkey

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(
    name = Data.PREFERENCES
)

class Data {

    companion object {

        const val PREFERENCES = "preferences"
        const val UNKNOWN = "Unknown"

        suspend fun readKey(c: Context, key: String, defaultValue: String): String {
            val map = c.dataStore.data
                .map { preferences ->
                    preferences[stringPreferencesKey(key)] ?: defaultValue
                }
            return map.first().toString()
        }

        suspend fun setKeyValue(c: Context, key: String, value: String) {
            c.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        }

    }

}