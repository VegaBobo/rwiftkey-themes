package com.rswiftkey

import android.content.Context

class SKeyboard(public val keyboards: List<SimpleApplication> = listOf()) : SKeyboardDS() {

    fun hasKeyboardsAvailable(): Boolean {
        return keyboards.isNotEmpty()
    }

    fun hasNoKeyboardsAvailable(): Boolean {
        return keyboards.isEmpty()
    }

    suspend fun getPackage(c: Context): String {
        val app = obtainTargetKeyboard(c)
        return app.packageName
    }

    suspend fun getName(c: Context): String {
        val app = obtainTargetKeyboard(c)
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