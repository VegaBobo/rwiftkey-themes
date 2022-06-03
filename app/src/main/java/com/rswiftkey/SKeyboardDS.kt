package com.rswiftkey

import android.content.Context

open class SKeyboardDS {

    companion object {
        const val PREF_TARGET_APPNAME = "target_appname"
        const val PREF_TARGET_PACKAGE = "target_package"
    }

    suspend fun setTargetKeyboard(c: Context, ka: SimpleApplication) {
        Data.setKeyValue(c, PREF_TARGET_APPNAME, ka.applicationName)
        Data.setKeyValue(c, PREF_TARGET_PACKAGE, ka.packageName)
    }

    suspend fun readTargetKeyboard(c: Context): SimpleApplication {
        return SimpleApplication(
            Data.readKey(c, PREF_TARGET_APPNAME, Data.UNKNOWN),
            Data.readKey(c, PREF_TARGET_PACKAGE, Data.UNKNOWN)
        )
    }

}