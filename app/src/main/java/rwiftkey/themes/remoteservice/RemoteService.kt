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
            fun remoteCallbackAction(action: IRemoteServiceCallbacks.() -> Unit) {
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
                    remoteCallbackAction(action)
                }
            }

            // Secure wrapper for oncoming actions
            fun selfCallbackAction(action: IHomeCallbacks. () -> Unit) {
                if (selfCallback == null) return
                try {
                    action(selfCallback!!)
                } catch (e: Exception) {
                    // ignore
                }
            }

            fun settingsCallbackAction(action: ISettingsCallbacks. () -> Unit) {
                if (settingsCallback == null) return
                try {
                    action(settingsCallback!!)
                } catch (e: Exception) {
                    // ignore
                }
            }

            // When remote tells our app that a rebind is required.
            fun callSelfToRebind() {
                selfCallbackAction {
                    onRemoteRequestRebind()
                }
            }

            // CALLED BY REMOTE

            override fun registerRemoteCallbacks(callback: IRemoteServiceCallbacks) {
                logd(this, "registerRemoteCallbacks()")
                remoteCallback = callback

                // load installed themes when callback is registered.
                remoteCallbackAction { onThemesRequest() }
            }

            override fun removeRemoteCallbacks() {
                logd(this, "removeRemoteCallbacks()")
                remoteCallback = null
            }

            override fun sendThemesToSelf(themes: List<Theme>?) {
                logd(this, "sendThemesToSelf()", themes.toString())
                selfCallbackAction { onReceiveThemes(themes) }
            }

            override fun onRemoteServiceStarted() {
                logd(this, "onRemoteServiceStarted()")
                selfCallbackAction { onRemoteBoundService() }
            }

            override fun onInstallThemeFromUriResult(hasInstalled: Boolean) {
                logd(this, "onInstallThemeFromUriResult()")
                selfCallbackAction { onInstallThemeResult(hasInstalled) }

                remoteCallback = null
                selfCallbackAction { onRemoteRequestRebind() }
            }

            override fun onFinishModifyTheme() {
                logd(this, "onFinishModifyTheme()")
                selfCallbackAction { onFinishModifyTheme() }

                remoteCallback = null
                selfCallbackAction { onRemoteRequestRebind() }
            }

            override fun onFinishDeleteTheme() {
                logd(this, "onFinishDeleteTheme()")
                selfCallbackAction { onFinishDeleteTheme() }

                remoteCallback = null
                selfCallbackAction { onRemoteRequestRebind() }
            }

            override fun requestUnbind() {
                logd(this, "requestUnbind()")
                remoteCallbackAction { onRequestUnbind() }
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
                remoteCallbackAction { onInstallThemeRequest(uri) }
            }

            override fun requestModifyTheme(themeId: String?, uri: Uri?) {
                logd(this, "requestModifyTheme()")
                remoteCallbackAction { onRequestModifyTheme(themeId, uri) }
            }

            override fun requestDeleteTheme(themeId: String) {
                logd(this, "requestDeleteTheme()")
                remoteCallbackAction { onRequestThemeDelete(themeId) }
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
                remoteCallbackAction { onRequestCleanup() }
            }

            override fun onRequestCleanupFinish() {
                logd(this, "onRequestCleanupFinish()")
                settingsCallbackAction { onRequestCleanupFinish() }

                remoteCallback = null
                settingsCallbackAction { onRemoteRequestRebind() }
            }
        }
    }
}
