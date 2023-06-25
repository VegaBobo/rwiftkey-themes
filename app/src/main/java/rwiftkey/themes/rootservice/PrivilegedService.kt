package rwiftkey.themes.rootservice

import rwiftkey.themes.IPrivilegedService
import android.os.Process

class PrivilegedService : IPrivilegedService.Stub() {
    override fun getUid(): Int {
        return Process.myUid()
    }
}