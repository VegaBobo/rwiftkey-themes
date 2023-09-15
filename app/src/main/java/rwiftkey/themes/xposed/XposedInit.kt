package rwiftkey.themes.xposed

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.os.RemoteException
import android.util.Log
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
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.IRemoteService
import rwiftkey.themes.IRemoteServiceCallback
import rwiftkey.themes.core.Operations
import kotlin.system.exitProcess


object IntentAction {
    const val THEME_FILE_URI = "themeFileUri"
    const val CLEAN_UP = "cleanup"
    const val OPEN_THEME_SECTION = "openThemesSection"
    const val READ_THEMES = "readThemes"
    const val EXIT_PROCESS = "exitProcess"
    const val FINISH = "finish"

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

                REMOTE_SERVICE!!.registerRemoteCallback(
                    object : IRemoteServiceCallback.Stub() {
                        override fun onThemesRequest() {
                            val themes = Operations.retrieveThemes(lpparam.packageName)
                            REMOTE_SERVICE!!.sendThemesToSelf(themes)
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
            }
        })

//        XposedBridge.hookAllMethods(launcherClazz, "onCreate", object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                super.afterHookedMethod(param)
//
//                val thisActivity = param!!.thisObject as Activity
//
//                // Dialog
//
//                val builder = AlertDialog.Builder(thisActivity)
//
//                builder.setTitle("Processing")
//                builder.setMessage("Please wait...")
//
//                val progressBar =
//                    ProgressBar(thisActivity, null, android.R.attr.progressBarStyleHorizontal)
//                progressBar.isIndeterminate = true
//                progressBar.setPadding(48, 0, 48, 0)
//                val lp = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                progressBar.layoutParams = lp
//                builder.setView(progressBar)
//                val dialog = builder.show()
//
//                // Operations
//
//                @Suppress("DEPRECATION")
//                val themeUri =
//                    if (Build.VERSION.SDK_INT >= 33)
//                        bundleFromStartup?.getParcelable(
//                            IntentAction.THEME_FILE_URI,
//                            Uri::class.java
//                        )
//                    else
//                        bundleFromStartup?.getParcelable(IntentAction.THEME_FILE_URI) as Uri?
//
//                val shouldClean = bundleFromStartup?.getBoolean(IntentAction.CLEAN_UP) ?: false
//
//                val finish = bundleFromStartup?.getBoolean(IntentAction.FINISH) ?: false
//
//                val openThemesSection =
//                    bundleFromStartup?.getBoolean(IntentAction.OPEN_THEME_SECTION) ?: false
//
//                val exitProcess = bundleFromStartup?.getBoolean(IntentAction.EXIT_PROCESS) ?: false
//
//                val readThemes = bundleFromStartup?.getBoolean(IntentAction.READ_THEMES) ?: false
//
//                if (themeUri != null)
//                    Operations.installTheme(thisActivity.application, lpparam.packageName, themeUri)
//
//                if (shouldClean)
//                    Operations.cleanUp(lpparam.packageName)
//
//                dialog.dismiss()
//
//                // TODO PROPER REMOTE SERVICE BIND
//                if (readThemes) {
//                    Log.i(BuildConfig.APPLICATION_ID, "readThemes()")
//
//                    var REMOTE_SERVICE: IRemoteService?
//
//                    val serviceConnection = object : ServiceConnection {
//                        override fun onServiceConnected(name: ComponentName, service: IBinder) {
//                            Log.i(BuildConfig.APPLICATION_ID, "onServiceConnected")
//                            REMOTE_SERVICE = IRemoteService.Stub.asInterface(service)
//
//                            REMOTE_SERVICE!!.registerRemoteCallback(
//                                object : IRemoteServiceCallback.Stub() {
//                                    override fun onThemesRequest() {
//                                        val themes = Operations.retrieveThemes(lpparam.packageName)
//                                        REMOTE_SERVICE!!.sendThemesToSelf(themes)
//                                    }
//                                }
//                            )
//
//                            REMOTE_SERVICE!!.ping()
//
//                        }
//
//                        override fun onServiceDisconnected(name: ComponentName) {
//                            Log.i(BuildConfig.APPLICATION_ID, "onServiceDisconnected")
//                            REMOTE_SERVICE = null
//                        }
//                    }
//
//                    val intent = Intent("${BuildConfig.APPLICATION_ID}.REMOTESERVICE")
//                    intent.setPackage(BuildConfig.APPLICATION_ID)
//                    thisActivity.bindService(intent, serviceConnection, BIND_AUTO_CREATE)
//                }
//
//                if (openThemesSection)
//                    Operations.openThemesSection(thisActivity)
//
//                if (finish)
//                    thisActivity.finishAffinity()
//
//                if (exitProcess) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        delay(1000)
//                        val pid = Process.myPid()
//                        Process.killProcess(pid)
//                        exitProcess(0)
//                    }
//                }
//
//            }
//        })
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam != null && !supportedPackages.contains(lpparam.packageName))
            return
        captureInitBundle(lpparam)
        onCreateHook(lpparam)
    }

}