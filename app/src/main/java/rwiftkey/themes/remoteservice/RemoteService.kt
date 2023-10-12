package rwiftkey.themes.remoteservice

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.DeadObjectException
import android.os.IBinder
import android.os.Process
import android.util.Log
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.IRemoteService
import rwiftkey.themes.IRemoteServiceCallbacks
import rwiftkey.themes.IHomeCallbacks
import rwiftkey.themes.ISettingsCallbacks
import rwiftkey.themes.ui.screen.home.KeyboardTheme


class RemoteService : Service() {

    var remoteCallback: IRemoteServiceCallbacks? = null

    var settingsCallback: ISettingsCallbacks? = null
    var selfCallback: IHomeCallbacks? = null
    override fun onBind(p0: Intent?): IBinder {
        return object : IRemoteService.Stub() {
            override fun ping() {
                val pid = Process.myPid()
                val uid = Process.myUid()
                Log.d(BuildConfig.APPLICATION_ID, "ping(): pid=$pid, uid=$uid")
            }

            fun remoteCallbackOperation(action: IRemoteServiceCallbacks.() -> Unit) {
                var ticks = 0
                while (remoteCallback == null) {
                    Log.d(
                        BuildConfig.APPLICATION_ID, "remoteCallback is not available, waiting.."
                    )
                    ticks++
                    Thread.sleep(200)
                    if (ticks > 10) {
                        Log.d(
                            BuildConfig.APPLICATION_ID,
                            "remoteCallback is not available for a long time, trying to rebind..."
                        )
                        callSelfToRebind()
                        ticks = 0
                    }
                }

                try {
                    action(remoteCallback!!)
                } catch (e: DeadObjectException) {
                    Log.e(BuildConfig.APPLICATION_ID, "remoteCallback is dead, trying to rebind...")
                    callSelfToRebind()
                    remoteCallbackOperation(action)
                }
            }

            fun callSelfToRebind() {
                remoteCallback = null
                if (selfCallback != null) {
                    selfCallback!!.onRemoteRequestRebind()
                } else {
                    throw Exception("remoteCallback is not available")
                }
            }

            // CALLED BY REMOTE

            override fun registerRemoteCallbacks(callback: IRemoteServiceCallbacks) {
                Log.d(BuildConfig.APPLICATION_ID, "registerCallback()")
                remoteCallback = callback

                // load installed themes when callback is registered.
                remoteCallbackOperation { onThemesRequest() }
            }

            override fun removeRemoteCallbacks() {
                Log.d(BuildConfig.APPLICATION_ID, "removeRemoteCallback()")
                remoteCallback = null
            }

            override fun sendThemesToSelf(themes: MutableList<KeyboardTheme>?) {
                Log.d(BuildConfig.APPLICATION_ID, "sendThemesToSelf(), themes: $themes")
                selfCallback!!.onReceiveThemes(themes)
            }

            override fun onRemoteServiceStarted() {
                Log.d(BuildConfig.APPLICATION_ID, "onRemoteServiceStarted()")
                selfCallback!!.onRemoteBoundService()
            }

            override fun onInstallThemeFromUriResult(hasInstalled: Boolean) {
                Log.d(BuildConfig.APPLICATION_ID, "onInstallThemeFromUriResult()")
                selfCallback!!.onInstallThemeResult(hasInstalled)

                remoteCallback = null
                selfCallback!!.onRemoteRequestRebind()
            }

            override fun onFinishModifyTheme() {
                Log.d(BuildConfig.APPLICATION_ID, "onFinishModifyTheme()")
                selfCallback!!.onFinishModifyTheme()
            }

            override fun onFinishDeleteTheme() {
                Log.d(BuildConfig.APPLICATION_ID, "onFinishDeleteTheme()")
                selfCallback!!.onFinishDeleteTheme()

                remoteCallback = null
                selfCallback!!.onRemoteRequestRebind()
            }

            override fun requestUnbind() {
                Log.d(BuildConfig.APPLICATION_ID, "requestUnbind()")
                remoteCallbackOperation { onRequestUnbind() }
            }

            // CALLED BY HOME

            override fun registerHomeCallbacks(callback: IHomeCallbacks) {
                Log.d(BuildConfig.APPLICATION_ID, "registerSelfCallbacks()")
                selfCallback = callback
            }

            override fun removeHomeCallbacks() {
                Log.d(BuildConfig.APPLICATION_ID, "removeSelfCallback()")
                selfCallback = null
            }

            override fun requestInstallThemeFromUri(uri: Uri?) {
                Log.d(BuildConfig.APPLICATION_ID, "requestInstallThemeFromUri()")
                if (uri == null) {
                    Log.d(
                        BuildConfig.APPLICATION_ID,
                        "requestInstallThemeFromUri(), uri is null, cannot proceed."
                    )
                    return
                }
                remoteCallbackOperation { onInstallThemeRequest(uri) }
            }

            override fun requestModifyTheme(themeId: String?, uri: Uri?) {
                Log.d(BuildConfig.APPLICATION_ID, "requestModifyTheme()")
                if (uri == null) {
                    Log.d(
                        BuildConfig.APPLICATION_ID,
                        "removeSelfCallback(), uri is null, cannot proceed."
                    )
                    return
                }
                remoteCallbackOperation { onRequestModifyTheme(themeId, uri) }
            }

            override fun requestDeleteTheme(themeId: String) {
                Log.d(BuildConfig.APPLICATION_ID, "requestDeleteTheme()")
                remoteCallbackOperation { onRequestThemeDelete(themeId) }
            }

            // CALLED BY SETTINGS

            override fun registerSettingsCallbacks(callback: ISettingsCallbacks?) {
                Log.d(BuildConfig.APPLICATION_ID, "registerSettingsCallback()")
                settingsCallback = callback
            }

            override fun removeSettingsCallbacks() {
                Log.d(BuildConfig.APPLICATION_ID, "removeSettingsCallback()")
                settingsCallback = null
            }

            override fun requestCleanup() {
                Log.d(BuildConfig.APPLICATION_ID, "requestCleanup()")
                remoteCallbackOperation { onRequestCleanup() }
            }

            override fun onRequestCleanupFinish() {
                settingsCallback!!.onRequestCleanupFinish()

                remoteCallback = null
                settingsCallback!!.onRemoteRequestRebind()
            }

        }
    }

}
