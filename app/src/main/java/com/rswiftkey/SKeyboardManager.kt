package com.rswiftkey

import android.content.Context
import android.util.Log

class SKeyboardManager(
    private val ctx: Context,
    val keyboards: ArrayList<SimpleApplication> = arrayListOf()
) : SKeyboardPrefUtils() {

    private val targetPackages = arrayListOf(
        SimpleApplication("Swiftkey", "com.touchtype.swiftkey"),
        SimpleApplication("Swiftkey Beta", "com.touchtype.swiftkey.beta"),
    )

    private fun loadAvailableKeyboards() {
        for (tp in targetPackages) {
            try {
                ctx.packageManager.getPackageInfo(tp.packageName, 0)
                keyboards.add(tp)
                if (BuildConfig.DEBUG)
                    Log.i("obtainSwiftKeyInstallation", tp.packageName)
            } catch (_: Exception) {
            }
        }
    }

    init {
        loadAvailableKeyboards()
    }

    fun hasKeyboardsAvailable(): Boolean {
        return keyboards.isNotEmpty()
    }

    fun hasNoKeyboardsAvailable(): Boolean {
        return keyboards.isEmpty()
    }

    suspend fun getPackage(): String {
        val app = obtainTargetKeyboard(ctx)
        return app.packageName
    }

    suspend fun getName(): String {
        val app = obtainTargetKeyboard(ctx)
        return app.applicationName
    }

    suspend fun obtainTargetKeyboard(c: Context): SimpleApplication {

        // if user has no available keyboards, return empty SimpleApplication
        if (hasNoKeyboardsAvailable()) return SimpleApplication()

        // try to match same keyboard information stored in DataStore
        // with the same one within this object, if they match, return it
        // we do that, to make sure that Swiftkey app stored in DataStore is installed on device
        val target = readTargetKeyboard(c)
        for (keyboard in keyboards)
            if (target.packageName == keyboard.packageName)
                return target

        // if they not match, info stored in DataStore is targeting
        // an application that aren't installed on device
        // maybe user has uninstalled app, so, we will retarget available app
        // and return it, since it is available
        val targetKeyboard = SimpleApplication(
            keyboards.first().applicationName,
            keyboards.first().packageName,
        )
        setTargetKeyboard(c, targetKeyboard)
        return targetKeyboard
    }

}