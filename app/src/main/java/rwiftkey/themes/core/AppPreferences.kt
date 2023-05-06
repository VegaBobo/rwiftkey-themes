package rwiftkey.themes.core

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import rwiftkey.themes.model.SimpleApplication

open class AppPreferences(
    val ds: DataStore<Preferences>
) {

    companion object {
        const val PREF_TARGET_APPNAME = "target_appname"
        const val PREF_TARGET_PACKAGE = "target_package"

        const val PREF_USE_XPOSED = "use_xposed"
    }

    suspend fun setTargetKeyboard(
        targetKeyboard: SimpleApplication,
        onFinish: () -> Unit = {}
    ) {
        ds.edit {
            it[stringPreferencesKey(PREF_TARGET_APPNAME)] = targetKeyboard.applicationName
            it[stringPreferencesKey(PREF_TARGET_PACKAGE)] = targetKeyboard.packageName
            return@edit
        }
        onFinish()
    }

    suspend fun readTargetKeyboard(): SimpleApplication {
        val keyboard = ds.data.map {
            val appName = it[stringPreferencesKey(PREF_TARGET_APPNAME)] ?: ""
            val targetPackage = it[stringPreferencesKey(PREF_TARGET_PACKAGE)] ?: ""
            Pair(appName, targetPackage)
        }.first()
        return SimpleApplication(keyboard.first, keyboard.second)
    }

    suspend fun setUseXposed() {
        ds.edit {
            it[booleanPreferencesKey(PREF_USE_XPOSED)] = true
            return@edit
        }
    }

    suspend fun readUseXposed(): Boolean {
        return ds.data.map {
            it[booleanPreferencesKey(PREF_USE_XPOSED)] ?: false
        }.first()
    }

}