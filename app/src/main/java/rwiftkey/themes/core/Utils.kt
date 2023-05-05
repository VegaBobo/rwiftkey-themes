package rwiftkey.themes.core

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.beust.klaxon.Klaxon
import com.topjohnwu.superuser.Shell
import rwiftkey.themes.model.Theme
import rwiftkey.themes.model.Themes
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class Utils {

    companion object {

        fun copyFile(
            context: Context,
            inputFile: Uri,
            outputFilename: String
        ): Uri {
            val input: InputStream?
            val output: OutputStream?
            val finalFile: DocumentFile =
                DocumentFile.fromFile(File(outputFilename))
            try {
                File(context.cacheDir.path + "/theme").mkdir()
                output = context.contentResolver.openOutputStream(finalFile.uri)
                input = context.contentResolver.openInputStream(inputFile)
                val buffer = ByteArray(1024)
                var read: Int
                while (input!!.read(buffer).also { read = it } != -1) {
                    output!!.write(buffer, 0, read)
                }
                input.close()
                output!!.flush()
                output.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return finalFile.uri
        }

        fun unzip(zipFilePath: String, destDir: String) {
            val buffer = ByteArray(1024)
            val zis = ZipInputStream(FileInputStream(zipFilePath))
            var zipEntry = zis.nextEntry
            while (zipEntry != null) {
                val newFile: File = newFile(File(destDir), zipEntry)!!
                if (zipEntry.isDirectory) {
                    if (!newFile.isDirectory && !newFile.mkdirs()) {
                        throw IOException("Failed to create directory $newFile")
                    }
                } else {
                    val parent = newFile.parentFile
                    if (!parent.isDirectory && !parent.mkdirs()) {
                        throw IOException("Failed to create directory $parent")
                    }
                    val fos = FileOutputStream(newFile)
                    var len: Int
                    while (zis.read(buffer).also { len = it } > 0) {
                        fos.write(buffer, 0, len)
                    }
                    fos.close()
                }
                zipEntry = zis.nextEntry
            }
            zis.closeEntry()
            zis.close()
        }

        private fun newFile(destinationDir: File, zipEntry: ZipEntry): File {
            val destFile = File(destinationDir, zipEntry.name)
            val destDirPath = destinationDir.canonicalPath
            val destFilePath = destFile.canonicalPath
            if (!destFilePath.startsWith(destDirPath + File.separator)) {
                throw IOException("Entry is outside of the target dir: " + zipEntry.name)
            }
            return destFile
        }

        fun jsonToThemeObject(inputString: String): List<Theme> {
            val themeArray: ArrayList<Theme> = ArrayList()
            val theme = Klaxon().parse<Themes>(inputString)
            for (t in theme!!.themes) {
                themeArray.add(t)
            }
            return themeArray
        }

        fun startSKActivity(targetPackage: String) {
            Shell
                .cmd("am start $targetPackage/com.touchtype.materialsettings.themessettings.ThemeSettingsActivity")
                .exec()
        }

    }

}

fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags)
    }