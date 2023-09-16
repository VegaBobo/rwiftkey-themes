package rwiftkey.themes.remoteservice

import android.app.Service
import android.content.Intent
import android.net.Uri
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

            // CALLED BY REMOTE

            override fun registerRemoteCallbacks(callback: IRemoteServiceCallbacks) {
                Log.d(BuildConfig.APPLICATION_ID, "registerCallback()")
                remoteCallback = callback

                // load installed themes when callback is registered.
                remoteCallback!!.onThemesRequest()
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
                selfCallback!!.onRemoteRequestRebind()
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
                Log.d(BuildConfig.APPLICATION_ID, "removeSelfCallback()")
                if (uri == null) {
                    Log.d(
                        BuildConfig.APPLICATION_ID,
                        "removeSelfCallback(), uri is null, cannot proceed."
                    )
                    return
                }
                remoteCallback!!.onInstallThemeRequest(uri)
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
                remoteCallback!!.onRequestCleanup()
            }

            override fun onRequestCleanupFinish() {
                settingsCallback!!.onRequestCleanupFinish()
                settingsCallback!!.onRemoteRequestRebind()
            }

        }
    }

}
