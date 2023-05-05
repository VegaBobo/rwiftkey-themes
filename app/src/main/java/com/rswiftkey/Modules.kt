package com.rswiftkey

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

object AppConstants {
    const val PREFERENCES = "preferences"
}

@InstallIn(SingletonComponent::class)
@Module
class Modules {

    @Singleton
    @Provides
    fun provideKeyboardManager(@ApplicationContext appContext: Context, dataStore: DataStore<Preferences>): SKeyboardManager {
        return SKeyboardManager(appContext, dataStore)
    }

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() },
            ),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(AppConstants.PREFERENCES) }
        )
    }

    @Singleton
    @Provides
    fun appPrefs(dataStore: DataStore<Preferences>): AppPreferences {
        return AppPreferences(dataStore)
    }

}