package rwiftkey.themes

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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


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

    fun installThemeHook(lpparam: XC_LoadPackage.LoadPackageParam?) {
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

                val themeUri =
                    if (Build.VERSION.SDK_INT >= 33)
                        bundleFromStartup?.getParcelable("themeFileUri", Uri::class.java)
                    else
                        bundleFromStartup?.getParcelable("themeFileUri") as Uri?

                val shouldClean = bundleFromStartup?.getBoolean("cleanup") ?: false

                val finish = bundleFromStartup?.getBoolean("finish") ?: false

                val openThemesSection = bundleFromStartup?.getBoolean("openThemesSection") ?: false

                val exitProcess = bundleFromStartup?.getBoolean("exitProcess") ?: false

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
        installThemeHook(lpparam)
    }

}