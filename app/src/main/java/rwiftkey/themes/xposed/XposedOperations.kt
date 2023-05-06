package rwiftkey.themes.xposed

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.beust.klaxon.Klaxon
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.core.jsonToThemeObject
import rwiftkey.themes.core.unzip
import rwiftkey.themes.model.Theme
import rwiftkey.themes.model.Themes
import java.io.BufferedReader
import java.io.File

fun installTheme(app: Application, themeUri: Uri) {
    val targetDir = File(app.filesDir.absolutePath + "/custom_themes/wip")
    if (!targetDir.exists())
        targetDir.mkdir()
    unzip(app, themeUri, targetDir)
    mergeJsons(app)
}

fun mergeJsons(app: Application) {
    // Process existing themes
    val originalJson =
        app.filesDir.absolutePath + "/custom_themes/themelist_custom.json"
    val originalJsonAsString = filePathToString(originalJson)

    val themes: ArrayList<Theme> = ArrayList()
    themes.addAll(jsonToThemeObject(originalJsonAsString))

    // Process newly installed theme(s)
    val themesToBeAppended =
        app.filesDir.absolutePath + "/custom_themes/wip/themelist_custom.json"
    val jsonToBeAppendedAsString = filePathToString(themesToBeAppended)
    themes.addAll(jsonToThemeObject(jsonToBeAppendedAsString))

    // Merge
    val finalJson = Klaxon().toJsonString(Themes(themes.distinctBy { it.id }))
    File(originalJson).writeText(finalJson)

    // move wip to proper folder
    val wipFolder =
        File(app.filesDir.absolutePath + "/custom_themes/wip")
    for (f in wipFolder.listFiles() ?: return) {
        if (!f.isDirectory)
            continue
        val dir = File(f.parentFile.parentFile.absolutePath + "/${f.name}")
        dir.mkdir()
        f.copyRecursively(dir, true)
    }

    // cleanup wip folder
    wipFolder.deleteRecursively()
}

fun cleanUp(app: Application) {
    val targetDir = app.filesDir.absolutePath + "/custom_themes"
    for (file in File(targetDir).listFiles() ?: return) {
        file.deleteRecursively()
    }
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

fun openThemesSection(mainAc: Activity) {
    val i = Intent()
    i.setClassName(
        mainAc.packageName,
        "com.touchtype.materialsettings.themessettings.ThemeSettingsActivity"
    )
    mainAc.startActivity(i)
}