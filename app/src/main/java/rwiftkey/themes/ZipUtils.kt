package rwiftkey.themes

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


fun unzip(context: Context, zipFile: Uri, location: File) {
    context.contentResolver.openFileDescriptor(zipFile, "r").use { descriptor ->
        descriptor?.fileDescriptor?.let {
            ZipInputStream(BufferedInputStream(FileInputStream(it))).use { inStream ->
                unzip(inStream, location)
            }
        }
    }
}

private fun unzip(inStream: ZipInputStream, location: File) {
    if (location.exists() && !location.isDirectory)
        throw IllegalStateException("Location file must be directory or not exist")

    if (!location.isDirectory) location.mkdirs()

    val locationPath = location.absolutePath.let {
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