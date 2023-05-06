package rwiftkey.themes.core

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import com.beust.klaxon.Klaxon
import com.topjohnwu.superuser.Shell
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.xposed.IntentAction
import rwiftkey.themes.model.Theme
import rwiftkey.themes.model.Themes
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }

fun Context.copyFile(input: Uri, output: Uri): Boolean? {
    try {
        val out = this.contentResolver.openOutputStream(output)
        val inp = this.contentResolver.openInputStream(input)
        val buffer = ByteArray(1024)
        var read: Int
        while (inp!!.read(buffer).also { read = it } != -1) {
            out!!.write(buffer, 0, read)
        }
        inp.close()
        out!!.flush()
        out.close()
        return true
    } catch (e: Exception) {
        Log.e(BuildConfig.APPLICATION_ID, "Copy operation failed: \n${e.stackTraceToString()}")
        return null
    }
}

fun jsonToThemeObject(inputString: String): List<Theme> {
    val themeArray: ArrayList<Theme> = ArrayList()
    val theme = Klaxon().parse<Themes>(inputString)
    for (t in theme!!.themes) {
        themeArray.add(t)
    }
    return themeArray
}

fun shellStartSKActivity(targetPackage: String) {
    Shell
        .cmd("am start $targetPackage/com.touchtype.materialsettings.themessettings.ThemeSettingsActivity")
        .exec()
}

fun Context.startSKActivity(
    targetPackage: String,
    uri: Uri?,
    vararg actions: String
) {
    val i = Intent()
    i.setClassName(targetPackage, "com.touchtype.LauncherActivity")
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (uri != null)
        i.putExtra(IntentAction.THEME_FILE_URI, uri)

    for (action in actions)
        i.putExtra(action, true)

    this.startActivity(i)
}

fun Context.startSKActivity(
    targetPackage: String,
    vararg actions: String
) {
    startSKActivity(targetPackage, null, *actions)
}

fun unzip(filePath: String, output: String) {
    unzip(File(filePath), File(output))
}

fun unzip(zipFile: File, output: File) {
    ZipInputStream(BufferedInputStream(FileInputStream(zipFile))).use { inStream ->
        unzip(inStream, output)
    }
}

fun unzip(context: Context, zipFile: Uri, output: File) {
    context.contentResolver.openFileDescriptor(zipFile, "r").use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipInputStream(BufferedInputStream(FileInputStream(it))).use { inStream ->
                unzip(inStream, output)
            }
        }
    }
}

private fun unzip(inStream: ZipInputStream, output: File) {
    if (output.exists() && !output.isDirectory)
        throw IllegalStateException("Location file must be directory or not exist")

    if (!output.isDirectory) output.mkdirs()

    val locationPath = output.absolutePath.let {
        if (!it.endsWith(File.separator)) "$it${File.separator}"
        else it
    }

    var zipEntry: ZipEntry?
    var unzipFile: File
    var unzipParentDir: File?

    while (inStream.nextEntry.also { zipEntry = it } != null) {
        unzipFile = File(locationPath + zipEntry!!.name)
        if (zipEntry!!.isDirectory) {
            if (!unzipFile.isDirectory) unzipFile.mkdirs()
        } else {
            unzipParentDir = unzipFile.parentFile
            if (unzipParentDir != null && !unzipParentDir.isDirectory) {
                unzipParentDir.mkdirs()
            }
            BufferedOutputStream(FileOutputStream(unzipFile)).use { outStream ->
                inStream.copyTo(outStream)
            }
        }
    }
}