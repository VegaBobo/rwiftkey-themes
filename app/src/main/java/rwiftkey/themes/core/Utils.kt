package rwiftkey.themes.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import com.beust.klaxon.Klaxon
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.model.Theme
import rwiftkey.themes.model.Themes
import rwiftkey.themes.xposed.IntentAction
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun loge(clazz: Any?, vararg args: Any?) {
    Log.e(
        "${BuildConfig.APPLICATION_ID} - ${clazz?.javaClass?.simpleName ?: "?"}",
        args.map { "$it " }.toString().trim()
    )
}

fun logw(clazz: Any?, vararg args: Any?) {
    Log.w(
        "${BuildConfig.APPLICATION_ID} - ${clazz?.javaClass?.simpleName ?: "?"}",
        args.map { "$it " }.toString().trim()
    )
}

fun logd(clazz: Any?, vararg args: Any?) {
    Log.d(
        "${BuildConfig.APPLICATION_ID} - ${clazz?.javaClass?.simpleName ?: "?"}",
        args.map { "$it " }.toString().trim()
    )
}

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

fun shellStartSKActivity(targetPackage: String, goBack: Boolean = false) {
    Shell
        .cmd("am start $targetPackage/com.touchtype.materialsettings.themessettings.ThemeSettingsActivity")
        .exec()

    if (goBack) {
        Shell.cmd("sleep 1 ; input keyevent 4").exec()
    }
}

// todo IntentAction.THEME_FILE_URI isn't used anymore
fun Context.startSKActivity(
    targetPackage: String,
    uri: Uri?,
    vararg actions: String
) {
    val i = Intent()
    i.setClassName(targetPackage, "com.touchtype.LauncherActivity")
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    if (uri != null)
        i.putExtra("IntentAction.THEME_FILE_URI", uri)

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

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

fun filePathToString(path: String): String {
    return try {
        val bufferedReader: BufferedReader =
            File(path).bufferedReader()
        bufferedReader.use { it.readText() }
    } catch (e: Exception) {
        Log.e(BuildConfig.APPLICATION_ID, "Cannot filepath to String: \n${e.stackTraceToString()}")
        ""
    }
}

fun mergeJsonThemes(originalJson: String, themesToBeAppended: String): String {
    val originalJsonAsString = filePathToString(originalJson)

    val themes: ArrayList<Theme> = ArrayList()
    themes.addAll(jsonToThemeObject(originalJsonAsString))

    val jsonToBeAppendedAsString = filePathToString(themesToBeAppended)
    themes.addAll(jsonToThemeObject(jsonToBeAppendedAsString))

    return Klaxon().toJsonString(Themes(themes.distinctBy { it.id }))
}

fun fileContentToString(filePath: String): String {
    return try {
        val bufferedReader: BufferedReader =
            File(filePath).bufferedReader()
        bufferedReader.use { it.readText() }
    } catch (e: Exception) {
        Log.e(BuildConfig.APPLICATION_ID, "Cannot filepath to String: \n${e.stackTraceToString()}")
        ""
    }
}

fun downloadFile(url: String, fileName: String) {
    URL(url).openStream().use { inp ->
        BufferedInputStream(inp).use { bis ->
            FileOutputStream(fileName).use { fos ->
                val data = ByteArray(1024)
                var count: Int
                while (bis.read(data, 0, 1024).also { count = it } != -1) {
                    fos.write(data, 0, count)
                }
            }
        }
    }
}

suspend fun requestRemoteBinding(
    targetPackageName: String,
    app: Application,
    shouldOpenThemes: Boolean = false
) {
    delay(200)
    val i = Intent()
    i.setClassName(targetPackageName, "com.touchtype.LauncherActivity")
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    i.putExtra(IntentAction.BIND, true)
    if (shouldOpenThemes)
        i.putExtra(IntentAction.OPEN_THEME_SECTION, true)
    app.startActivity(i)
}

fun File.calculateSHA1(): String {
    val digest = MessageDigest.getInstance("SHA-1")
    val inputStream = this.inputStream()
    val buffer = ByteArray(8192)
    var bytesRead: Int

    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
        digest.update(buffer, 0, bytesRead)
    }

    val sha1Bytes = digest.digest()
    val result = StringBuilder()
    sha1Bytes.forEach {
        result.append(String.format("%02x", it))
    }

    return result.toString()
}

fun hasConnection(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}
