package rwiftkey.themes.remoteservice

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.DeadObjectException
import android.os.IBinder
import android.os.Process
import rwiftkey.themes.IHomeCallbacks
import rwiftkey.themes.IRemoteService
import rwiftkey.themes.IRemoteServiceCallbacks
import rwiftkey.themes.ISettingsCallbacks
import rwiftkey.themes.core.logd
import rwiftkey.themes.core.loge
import rwiftkey.themes.model.Theme

class RemoteService : Service() {

    // callbacks implemented by Hooked app.
    var remoteCallback: IRemoteServiceCallbacks? = null

    // callbacks received by our app, from settings section.
    var settingsCallback: ISettingsCallbacks? = null

    // callbacks received by our app, from home section.
    var selfCallback: IHomeCallbacks? = null

    override fun onBind(p0: Intent?): IBinder {
        return object : IRemoteService.Stub() {

            // Debugging
            override fun ping() {
                val pid = Process.myPid()
                val uid = Process.myUid()
                logd(this, "ping(): pid=$pid, uid=$uid")
            }

            // Secure wrapper for self to remote operations.
            fun remoteCallbackOperation(action: IRemoteServiceCallbacks.() -> Unit) {
                var ticks = 0
                while (remoteCallback == null) {
                    logd(this, "remoteCallback is unavailable, waiting bind...")
                    ticks++
                    Thread.sleep(200)
                    if (ticks > 10) {
                        logd("remoteCallback is unavailable for a long time, trying to rebind...")
                        callSelfToRebind()
                        ticks = 0
                    }
                }

                try {
                    action(remoteCallback!!)
                } catch (e: DeadObjectException) {
                    loge("remoteCallback is dead, trying to rebind...")
                    callSelfToRebind()
                    remoteCallbackOperation(action)
                }
            }

            // When remote tells our app that a rebind is required.
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
                logd(this, "registerRemoteCallbacks()")
                remoteCallback = callback

                // load installed themes when callback is registered.
                remoteCallbackOperation { onThemesRequest() }
            }

            override fun removeRemoteCallbacks() {
                logd(this, "removeRemoteCallbacks()")
                remoteCallback = null
            }

            override fun sendThemesToSelf(themes: List<Theme>?) {
                logd(this, "sendThemesToSelf()", themes.toString())
                selfCallback!!.onReceiveThemes(themes)
            }

            override fun onRemoteServiceStarted() {
                logd(this, "onRemoteServiceStarted()")
                selfCallback!!.onRemoteBoundService()
            }

            override fun onInstallThemeFromUriResult(hasInstalled: Boolean) {
                logd(this, "onInstallThemeFromUriResult()")
                selfCallback!!.onInstallThemeResult(hasInstalled)

                remoteCallback = null
                selfCallback!!.onRemoteRequestRebind()
            }

            override fun onFinishModifyTheme() {
                logd(this, "onFinishModifyTheme()")
                selfCallback!!.onFinishModifyTheme()

                remoteCallback = null
                selfCallback!!.onRemoteRequestRebind()
            }

            override fun onFinishDeleteTheme() {
                logd(this, "onFinishDeleteTheme()")
                selfCallback!!.onFinishDeleteTheme()

                remoteCallback = null
                selfCallback!!.onRemoteRequestRebind()
            }

            override fun requestUnbind() {
                logd(this, "requestUnbind()")
                remoteCallbackOperation { onRequestUnbind() }
            }

            // CALLED BY HOME

            override fun registerHomeCallbacks(callback: IHomeCallbacks) {
                logd(this, "registerHomeCallbacks()")
                selfCallback = callback
            }

            override fun removeHomeCallbacks() {
                logd(this, "removeHomeCallbacks()")
                selfCallback = null
            }

            override fun requestInstallThemeFromUri(uri: Uri?) {
                logd(this, "requestInstallThemeFromUri()")
                remoteCallbackOperation { onInstallThemeRequest(uri) }
            }

            override fun requestModifyTheme(themeId: String?, uri: Uri?) {
                logd(this, "requestModifyTheme()")
                remoteCallbackOperation { onRequestModifyTheme(themeId, uri) }
            }

            override fun requestDeleteTheme(themeId: String) {
                logd(this, "requestDeleteTheme()")
                remoteCallbackOperation { onRequestThemeDelete(themeId) }
            }

            // CALLED BY SETTINGS

            override fun registerSettingsCallbacks(callback: ISettingsCallbacks?) {
                logd(this, "registerSettingsCallbacks()")
                settingsCallback = callback
            }

            override fun removeSettingsCallbacks() {
                logd(this, "removeSettingsCallbacks()")
                settingsCallback = null
            }

            override fun requestCleanup() {
                logd(this, "requestCleanup()")
                remoteCallbackOperation { onRequestCleanup() }
            }

            override fun onRequestCleanupFinish() {
                logd(this, "onRequestCleanupFinish()")
                settingsCallback!!.onRequestCleanupFinish()

                remoteCallback = null
                settingsCallback!!.onRemoteRequestRebind()
            }
        }
    }
}
