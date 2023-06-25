package rwiftkey.themes.rootservice

import android.content.Intent
import android.os.IBinder
import com.topjohnwu.superuser.ipc.RootService

class PrivilegedRootService : RootService() {
    override fun onBind(intent: Intent): IBinder {
        return PrivilegedService()
    }
}