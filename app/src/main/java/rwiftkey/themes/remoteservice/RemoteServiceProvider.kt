package rwiftkey.themes.remoteservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.IRemoteService

object RemoteServiceProvider {
    private val tag = this.javaClass.simpleName

    var REMOTE_SERVICE: IRemoteService? = null
    var isRemoteLikelyConnected = false

    var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.i(BuildConfig.APPLICATION_ID, "onServiceConnected")
            REMOTE_SERVICE = IRemoteService.Stub.asInterface(service)
            REMOTE_SERVICE!!.ping()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.i(BuildConfig.APPLICATION_ID, "onServiceDisconnected")
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
            var timeout = 0
            while (!isConnected()) {
                timeout += 1000
                if (timeout > 20000) {
                    Log.e(tag, "Service unavailable.")
                    onFail()
                    return@launch
                }
                delay(1000)
                Log.d(tag, "Service unavailable, checking again in 1s.. [${timeout / 1000}s/20s]")
            }
            Log.d(tag, "IRemoteService available.")
            onConnected(service())
        }
    }

    // Blocking
    fun getService(): IRemoteService {
        var timeout = 0
        while (!isConnected()) {
            timeout += 1000
            if (timeout > 20000) {
                throw Exception("Service unavailable.")
            }
            Thread.sleep(1000)
        }
        return REMOTE_SERVICE!!
    }

    fun isConnected(): Boolean {
        return REMOTE_SERVICE != null
    }
}