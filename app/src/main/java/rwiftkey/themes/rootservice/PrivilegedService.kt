package rwiftkey.themes.rootservice

import android.annotation.SuppressLint
import android.app.IActivityManager
import android.os.IBinder
import android.os.Process
import rwiftkey.themes.IPrivilegedService
import rwiftkey.themes.core.Operations
import rwiftkey.themes.core.unzip
import rwiftkey.themes.model.Theme
import java.io.File

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

    override fun getKeyboardThemes(targetKeyboardPackage: String?): MutableList<Theme> {
        if (targetKeyboardPackage == null) return ArrayList()
        val themes = Operations.retrieveThemes(targetKeyboardPackage)
        return themes.toMutableList()
    }

    override fun deleteTheme(targetKeyboardPackage: String?, themeId: String?) {
        if (themeId == null || targetKeyboardPackage == null) return
        Operations.deleteTheme(targetKeyboardPackage, themeId)
    }

    override fun modifyTheme(
        targetKeyboardPackage: String?,
        themeId: String?,
        absZipFileToApply: String?
    ) {
        Operations.modifyThemeRoot(targetKeyboardPackage!!, themeId!!, File(absZipFileToApply!!))
    }
}