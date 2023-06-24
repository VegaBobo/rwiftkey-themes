package rwiftkey.themes.xposed

import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.widget.LinearLayout
import android.widget.ProgressBar
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

object IntentAction {
    const val THEME_FILE_URI = "themeFileUri"
    const val CLEAN_UP = "cleanup"
    const val OPEN_THEME_SECTION = "openThemesSection"
    const val EXIT_PROCESS = "exitProcess"
    const val FINISH = "finish"
}


class XposedInit : IXposedHookLoadPackage {

    val supportedPackages =
        listOf("com.touchtype.swiftkey", "com.touchtype.swiftkey.beta")

    var bundleFromStartup: Bundle? = null

    fun captureInitBundle(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val launcherClazz =
            XposedHelpers.findClass(
                "com.touchtype.LauncherActivity",
                lpparam!!.classLoader
            )

        XposedBridge.hookAllMethods(launcherClazz, "onCreate", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisActivity = param!!.thisObject as Activity
                bundleFromStartup = thisActivity.intent.extras
            }
        })
    }

    fun onCreateHook(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val launcherClazz =
            XposedHelpers.findClass(
                "com.touchtype.materialsettingsx.NavigationActivity",
                lpparam!!.classLoader
            )

        XposedBridge.hookAllMethods(launcherClazz, "onCreate", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)

                val thisActivity = param!!.thisObject as Activity

                // Dialog

                val builder = AlertDialog.Builder(thisActivity)

                builder.setTitle("Processing")
                builder.setMessage("Please wait...")

                val progressBar =
                    ProgressBar(thisActivity, null, android.R.attr.progressBarStyleHorizontal)
                progressBar.isIndeterminate = true
                progressBar.setPadding(48, 0, 48, 0)
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                progressBar.layoutParams = lp
                builder.setView(progressBar)
                val dialog = builder.show()

                // Operations

                @Suppress("DEPRECATION")
                val themeUri =
                    if (Build.VERSION.SDK_INT >= 33)
                        bundleFromStartup?.getParcelable(
                            IntentAction.THEME_FILE_URI,
                            Uri::class.java
                        )
                    else
                        bundleFromStartup?.getParcelable(IntentAction.THEME_FILE_URI) as Uri?

                val shouldClean = bundleFromStartup?.getBoolean(IntentAction.CLEAN_UP) ?: false

                val finish = bundleFromStartup?.getBoolean(IntentAction.FINISH) ?: false

                val openThemesSection =
                    bundleFromStartup?.getBoolean(IntentAction.OPEN_THEME_SECTION) ?: false

                val exitProcess = bundleFromStartup?.getBoolean(IntentAction.EXIT_PROCESS) ?: false

                if (themeUri != null)
                    installTheme(thisActivity.application, themeUri)

                if (shouldClean)
                    cleanUp(thisActivity.application)

                dialog.dismiss()

                if (openThemesSection)
                    openThemesSection(thisActivity)

                if (finish)
                    thisActivity.finishAffinity()

                if (exitProcess) {
                    CoroutineScope(Dispatchers.IO).launch {
                        delay(1000)
                        val pid = Process.myPid()
                        Process.killProcess(pid)
                        exitProcess(0)
                    }
                }

            }
        })
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam != null && !supportedPackages.contains(lpparam.packageName))
            return
        captureInitBundle(lpparam)
        onCreateHook(lpparam)
    }

}