package rwiftkey.themes.core

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.model.SimpleApplication
import rwiftkey.themes.ui.screen.home.AppOperationMode

class SKeyboardManager(
    private val ctx: Context,
    dataStore: DataStore<Preferences>
) : AppPreferences(dataStore) {

    // SKeyboard apps installed on phone
    val availKeyboards: ArrayList<SimpleApplication> = arrayListOf()

    private val targetPackages = arrayListOf(
        SimpleApplication("Swiftkey", "com.touchtype.swiftkey"),
        SimpleApplication("Swiftkey Beta", "com.touchtype.swiftkey.beta"),
    )

    private fun loadAvailableKeyboards() {
        for (tp in targetPackages) {
            try {
                ctx.packageManager.getPackageInfoCompat(tp.packageName, 0)
                availKeyboards.add(tp)
                if (BuildConfig.DEBUG)
                    Log.i("obtainSwiftKeyInstallation", tp.packageName)
            } catch (_: Exception) {
            }
        }
    }

    var operationMode = AppOperationMode.XPOSED
    fun updateOperationMode(newOperationMode: AppOperationMode) {
        operationMode = newOperationMode
    }

    fun isRooted(): Boolean {
        return operationMode == AppOperationMode.ROOT
    }

    fun isXposed(): Boolean {
        return operationMode == AppOperationMode.XPOSED
    }

    init {
        loadAvailableKeyboards()
    }

    suspend fun startSKThemeAc() {
        shellStartSKActivity(getPackage())
    }

    fun hasKeyboardsAvailable(): Boolean {
        return availKeyboards.isNotEmpty()
    }

    fun hasNoKeyboardsAvailable(): Boolean {
        return availKeyboards.isEmpty()
    }

    suspend fun getPackage(): String {
        val app = obtainTargetKeyboard()
        return app.packageName
    }

    suspend fun getName(): String {
        val app = obtainTargetKeyboard()
        return app.applicationName
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