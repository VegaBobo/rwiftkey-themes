package rwiftkey.themes.rootservice

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rwiftkey.themes.IPrivilegedService
import rwiftkey.themes.core.Constants
import rwiftkey.themes.core.logd

object PrivilegedProvider {

    var ROOT_SERVICE: IPrivilegedService? = null

    private fun isConnected(): Boolean = ROOT_SERVICE != null

    var connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            logd(this, "onServiceConnected()")
            ROOT_SERVICE = IPrivilegedService.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            logd(this, "onServiceDisconnected()")
            ROOT_SERVICE = null
        }
    }

    fun run(
        onFail: () -> Unit = {},
        onConnected: suspend IPrivilegedService.() -> Unit,
    ) {
        fun service() = this.ROOT_SERVICE!!
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
            logd(this, "IPrivilegedService available, uid: ${service().uid}")
            onConnected(service())
        }
    }
}
