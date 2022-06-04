package com.rswiftkey.util

import android.content.Context
import android.util.Log
import com.rswiftkey.BuildConfig
import com.rswiftkey.SKeyboard
import com.rswiftkey.SimpleApplication

class KeyboardUtils {

    companion object {

        private val targetPackages = arrayListOf(
            SimpleApplication("Swiftkey", "com.touchtype.swiftkey"),
            SimpleApplication("Swiftkey Beta", "com.touchtype.swiftkey.beta"),
        )

        fun obtainSKeyboard(c: Context): SKeyboard {
            val availableKeyboards = arrayListOf<SimpleApplication>()
            for (tp in targetPackages) {
                try {
                    //Log.i("obtainSwiftKeyInstallation", tp.packageName)
                    c.packageManager.getPackageInfo(tp.packageName, 0)
                    availableKeyboards.add(tp)
                    if (BuildConfig.DEBUG)
                        Log.i("obtainSwiftKeyInstallation", tp.packageName)
                } catch (_: Exception) {
                }
            }
            return SKeyboard(availableKeyboards)
        }

        fun obtainKeyboards(c: Context): List<SimpleApplication> {
            return obtainSKeyboard(c).keyboards
        }

    }

}