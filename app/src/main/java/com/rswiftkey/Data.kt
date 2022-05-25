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

        private const val PREF_TARGET_APPNAME = "target_appname"
        private const val PREF_TARGET_PACKAGE = "target_package"

        const val PREFERENCES = "preferences"
        const val UNKNOWN = "Unknown"

        private suspend fun readKey(c: Context, key: String, defaultValue: String): String {
            val map = c.dataStore.data
                .map { preferences ->
                    preferences[stringPreferencesKey(key)] ?: defaultValue
                }
            return map.first().toString()
        }

        private suspend fun setKeyValue(c: Context, key: String, value: String) {
            c.dataStore.edit { preferences ->
                preferences[stringPreferencesKey(key)] = value
            }
        }

        suspend fun setTargetKeyboard(c: Context, ka: KeyboardApplication) {
            setKeyValue(c, PREF_TARGET_APPNAME, ka.applicationName)
            setKeyValue(c, PREF_TARGET_PACKAGE, ka.packageName)
        }

        suspend fun readTargetKeyboard(c: Context): KeyboardApplication {
            return KeyboardApplication(
                readKey(c, PREF_TARGET_APPNAME, UNKNOWN),
                readKey(c, PREF_TARGET_PACKAGE, UNKNOWN)
            )
        }

    }

}