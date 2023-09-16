package rwiftkey.themes.xposed

import android.app.Activity
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.IRemoteService
import rwiftkey.themes.IRemoteServiceCallbacks
import rwiftkey.themes.core.Operations
import kotlin.system.exitProcess


object IntentAction {
    const val OPEN_THEME_SECTION = "openThemesSection"
    const val BIND = "bind"
}


class XposedInit : IXposedHookLoadPackage {

    val supportedPackages =
        listOf("com.touchtype.swiftkey", "com.touchtype.swiftkey.beta")

    var bundleFromStartup: Bundle? = null

    fun bindService(hookedActivity: Activity, lpparam: XC_LoadPackage.LoadPackageParam) {
        var REMOTE_SERVICE: IRemoteService?

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                Log.i(BuildConfig.APPLICATION_ID, "onServiceConnected")
                REMOTE_SERVICE = IRemoteService.Stub.asInterface(service)

                REMOTE_SERVICE!!.registerRemoteCallbacks(
                    object : IRemoteServiceCallbacks.Stub() {
                        override fun onThemesRequest() {
                            val themes = Operations.retrieveThemes(lpparam.packageName)
                            REMOTE_SERVICE!!.sendThemesToSelf(themes)
                        }

                        override fun onInstallThemeRequest(uri: Uri) {
                            try {
                                Operations.installTheme(hookedActivity, lpparam.packageName, uri)
                                REMOTE_SERVICE!!.onInstallThemeFromUriResult(true)
                            } catch (e: Exception) {
                                Log.e(BuildConfig.APPLICATION_ID, e.stackTraceToString())
                                REMOTE_SERVICE!!.onInstallThemeFromUriResult(false)
                            }
                            exitProcess(0)
                        }

                        override fun onRequestCleanup() {
                            Operations.cleanUp(lpparam.packageName)
                            REMOTE_SERVICE!!.onRequestCleanupFinish()
                            exitProcess(0)
                        }
                    }
                )

                REMOTE_SERVICE!!.ping()
                REMOTE_SERVICE!!.onRemoteServiceStarted()
                hookedActivity.finish()
            }

            override fun onServiceDisconnected(name: ComponentName) {
                Log.i(BuildConfig.APPLICATION_ID, "onServiceDisconnected")
                REMOTE_SERVICE = null
            }
        }

        val intent = Intent("${BuildConfig.APPLICATION_ID}.REMOTESERVICE")
        intent.setPackage(BuildConfig.APPLICATION_ID)
        val hookedAppCtx = hookedActivity.applicationContext
        hookedAppCtx.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
    }

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

                val shouldBindService = bundleFromStartup?.getBoolean(IntentAction.BIND) ?: false
                if (shouldBindService) bindService(thisActivity, lpparam)

                val shouldOpenThemeSection =
                    bundleFromStartup?.getBoolean(IntentAction.OPEN_THEME_SECTION) ?: false
                if (shouldOpenThemeSection) Operations.openThemesSection(thisActivity)
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