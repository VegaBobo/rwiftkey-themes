package rwiftkey.themes.remoteservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rwiftkey.themes.IRemoteService
import rwiftkey.themes.core.Constants
import rwiftkey.themes.core.logd

object RemoteServiceProvider {

    var REMOTE_SERVICE: IRemoteService? = null
    private fun isConnected(): Boolean = REMOTE_SERVICE != null

    var isRemoteLikelyConnected = false

    var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            logd(this, "onServiceConnected()")
            REMOTE_SERVICE = IRemoteService.Stub.asInterface(service)
            REMOTE_SERVICE!!.ping()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            logd(this, "onServiceDisconnected()")
            REMOTE_SERVICE = null
        }
    }

    fun run(
        onFail: () -> Unit = {},
        onConnected: suspend IRemoteService.() -> Unit,
    ) {
        fun service() = REMOTE_SERVICE!!
        CoroutineScope(Dispatchers.IO).launch {
            if (isConnected()) {
                onConnected(service())
                return@launch
            }
            var timeout = 0L
            while (!isConnected()) {
                timeout += Constants.BIND_SERVICE_RETRY_DELAY_MS
                if (timeout > Constants.BIND_SERVICE_TIMEOUT_MS) {
                    logd(this, "Service unavailable.")
                    onFail()
                    return@launch
                }
                delay(Constants.BIND_SERVICE_RETRY_DELAY_MS)
                logd(this, "Service unavailable, checking again in 1s.. [${timeout / 1000}s/20s]")
            }
            logd(this, "IRemoteService available.")
            onConnected(service())
        }
    }
}
