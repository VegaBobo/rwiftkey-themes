package rwiftkey.themes.core

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import rwiftkey.themes.model.SimpleApplication
import rwiftkey.themes.ui.screen.home.AppOperationMode

class Session(
    private val app: Context,
    dataStore: DataStore<Preferences>
) : AppPreferences(dataStore) {

    // Keyboard apps installed on phone
    val availKeyboards: ArrayList<SimpleApplication> = arrayListOf()

    // Default operation mode
    var operationMode = AppOperationMode.NONE

    // Target keyboard app
    var targetKeyboardPackage = ""

    private val possibleKeyboards = arrayListOf(
        SimpleApplication("Swiftkey", "com.touchtype.swiftkey"),
        SimpleApplication("Swiftkey Beta", "com.touchtype.swiftkey.beta"),
    )

    private fun loadAvailableKeyboards() {
        for (kb in possibleKeyboards) {
            try {
                app.packageManager.getPackageInfoCompat(kb.packageName, 0)
                availKeyboards.add(kb)
                logd("loadAvailableKeyboards(): ${kb.packageName}")
            } catch (e: Exception) {
                logw(e.stackTraceToString())
            }
        }
    }

    fun isRooted(): Boolean = operationMode == AppOperationMode.ROOT

    fun isXposed(): Boolean = operationMode == AppOperationMode.XPOSED

    fun hasKeyboardsAvailable(): Boolean = availKeyboards.isNotEmpty()

    private fun hasNoKeyboardsAvailable(): Boolean = availKeyboards.isEmpty()

    init {
        loadAvailableKeyboards()
    }

    suspend fun updateTargetKeyboardPackage() {
        val app = obtainTargetKeyboard()
        targetKeyboardPackage = app.packageName
    }

    suspend fun obtainTargetKeyboard(): SimpleApplication {
        // if user has no available keyboards, return empty SimpleApplication
        if (hasNoKeyboardsAvailable()) return SimpleApplication()

        // try to match same keyboard information stored in DataStore
        // with the same one within this object, if they match, return it
        // we do that, to make sure that Swiftkey app stored in DataStore is installed on device
        val target = readTargetKeyboard()
        for (keyboard in availKeyboards)
            if (target.packageName == keyboard.packageName)
                return target

        // if they not match, info stored in DataStore is targeting
        // an application that aren't installed on device
        // maybe user has uninstalled app, so, we will retarget available app
        // and return it, since it is available
        val targetKeyboard = SimpleApplication(
            availKeyboards.first().applicationName,
            availKeyboards.first().packageName,
        )
        setTargetKeyboard(targetKeyboard)
        return targetKeyboard
    }
}
