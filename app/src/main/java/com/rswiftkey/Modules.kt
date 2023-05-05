package com.rswiftkey

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class Modules {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): SKeyboardManager {
        return SKeyboardManager(appContext)
    }

}