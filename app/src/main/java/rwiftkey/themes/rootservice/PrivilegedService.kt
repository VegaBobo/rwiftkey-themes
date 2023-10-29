package rwiftkey.themes.rootservice

import android.annotation.SuppressLint
import android.app.IActivityManager
import android.content.Intent
import android.os.IBinder
import android.os.Process
import com.topjohnwu.superuser.ipc.RootService
import java.io.File
import rwiftkey.themes.IPrivilegedService
import rwiftkey.themes.core.Operations
import rwiftkey.themes.core.logd
import rwiftkey.themes.model.Theme

class PrivilegedService : RootService() {

    override fun onBind(intent: Intent): IBinder {

        return object : IPrivilegedService.Stub() {

            // Internal

            @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
            private fun getBinder(service: String): IBinder {
                val serviceManager = Class.forName("android.os.ServiceManager")
                val method = serviceManager.getDeclaredMethod("getService", String::class.java)
                return method.invoke(null, service) as IBinder
            }

            override fun getUid(): Int {
                return Process.myUid()
            }

            // Privileged API

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

            // Service operations

            override fun installTheme(targetPackage: String, themePath: String?) {
                logd(this, "installTheme()")
                Operations.installTheme(targetPackage, themePath!!)
            }

            override fun cleanThemes(targetPackage: String) {
                logd(this, "cleanThemes()")
                Operations.cleanUp(targetPackage)
            }

            override fun getKeyboardThemes(targetPackage: String?): MutableList<Theme> {
                logd(this, "getKeyboardThemes()")
                val themes = Operations.retrieveThemes(targetPackage!!)
                return themes.toMutableList()
            }

            override fun deleteTheme(targetPackage: String?, themeId: String?) {
                logd(this, "deleteTheme()")
                Operations.deleteTheme(targetPackage!!, themeId!!)
            }

            override fun modifyTheme(targetPackage: String?, themeId: String?, absZip: String?) {
                logd(this, "modifyTheme()")
                Operations.modifyThemeRoot(targetPackage!!, themeId!!, File(absZip!!))
            }
        }
    }
}
