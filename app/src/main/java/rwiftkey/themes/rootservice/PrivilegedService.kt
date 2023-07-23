package rwiftkey.themes.rootservice

import android.annotation.SuppressLint
import android.app.IActivityManager
import android.os.IBinder
import android.os.Process
import rwiftkey.themes.IPrivilegedService
import rwiftkey.themes.core.Operations
import rwiftkey.themes.ui.screen.home.KeyboardTheme

class PrivilegedService : IPrivilegedService.Stub() {

    private fun getBinder(service: String): IBinder {
        val serviceManager = Class.forName("android.os.ServiceManager")
        val method = serviceManager.getDeclaredMethod("getService", String::class.java)
        return method.invoke(null, service) as IBinder
    }

    override fun getUid(): Int {
        return Process.myUid()
    }

    @SuppressLint("SdCardPath")
    override fun installTheme(targetKeyboardPackage: String, themePath: String?) {
        if (themePath.isNullOrEmpty())
            return

        Operations.installTheme(targetKeyboardPackage, themePath)
    }

    override fun cleanThemes(targetKeyboardPackage: String) {
        Operations.cleanUp(targetKeyboardPackage)
    }

    private var ACTIVITY_MANAGER: IActivityManager? = null

    private fun requiresActivityManager() {
        if (ACTIVITY_MANAGER == null) {
            ACTIVITY_MANAGER = IActivityManager.Stub.asInterface(getBinder("activity"))
        }
    }

    override fun forceStopPackage(packageName: String?) {
        requiresActivityManager()
        ACTIVITY_MANAGER!!.forceStopPackage(packageName, 0)
    }

    override fun getKeyboardThemes(targetKeyboardPackage: String?): MutableList<KeyboardTheme> {
        if(targetKeyboardPackage == null) return ArrayList()
        return Operations.retrieveThemes(targetKeyboardPackage)
    }
}