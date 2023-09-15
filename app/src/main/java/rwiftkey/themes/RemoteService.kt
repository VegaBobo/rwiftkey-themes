package rwiftkey.themes

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Process
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import rwiftkey.themes.ui.screen.home.KeyboardTheme
import java.lang.Thread.sleep


class RemoteService : Service() {

    var remoteCallback: IRemoteServiceCallback? = null
    var selfCallback: ISelfServiceCallback? = null
    override fun onBind(p0: Intent?): IBinder {
        return object : IRemoteService.Stub() {
            override fun ping() {
                val pid = Process.myPid()
                val uid = Process.myUid()
                Log.d(BuildConfig.APPLICATION_ID, "ping(): pid=$pid, uid=$uid")
            }

            // CALLED BY REMOTE

            override fun registerRemoteCallback(callback: IRemoteServiceCallback) {
                Log.d(BuildConfig.APPLICATION_ID, "registerCallback()")
                remoteCallback = callback

                // load installed themes when callback is registered.
                remoteCallback!!.onThemesRequest()
            }

            override fun removeRemoteCallback() {
                Log.d(BuildConfig.APPLICATION_ID, "removeRemoteCallback()")
                remoteCallback = null
            }

            override fun sendThemesToSelf(themes: MutableList<KeyboardTheme>?) {
                Log.d(BuildConfig.APPLICATION_ID, "sendThemesToSelf(), themes: $themes")
                selfCallback!!.onReceiveThemes(themes)
            }

            // CALLED BY SELF

            override fun registerSelfCallbacks(callback: ISelfServiceCallback) {
                Log.d(BuildConfig.APPLICATION_ID, "registerSelfCallbacks()")
                selfCallback = callback
            }

            override fun removeSelfCallback() {
                Log.d(BuildConfig.APPLICATION_ID, "removeSelfCallback()")
                selfCallback = null
            }

        }
    }

}
