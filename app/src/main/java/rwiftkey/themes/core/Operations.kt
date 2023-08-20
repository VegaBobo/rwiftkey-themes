package rwiftkey.themes.core

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.beust.klaxon.Klaxon
import rwiftkey.themes.model.Theme
import rwiftkey.themes.model.Themes
import rwiftkey.themes.ui.screen.home.KeyboardTheme
import java.io.File

object Operations {

    fun installTheme(
        ctx: Context,
        targetPackage: String,
        themeUri: Uri
    ) {
        installTheme(ctx, targetPackage, null, themeUri)
    }

    fun installTheme(
        targetPackage: String,
        themePath: String
    ) {
        installTheme(null, targetPackage, themePath, null)
    }

    private fun installTheme(
        ctx: Context? = null,
        targetPackage: String,
        themePath: String? = null,
        themeUri: Uri? = null
    ) {
        val customThemesFolderPath = "/data/data/$targetPackage/files/custom_themes"
        val customThemesFolderJson = "$customThemesFolderPath/themelist_custom.json"

        val wipFolderPath = "$customThemesFolderPath/wip"
        val wipFolderJsonPath = "$wipFolderPath/themelist_custom.json"

        val targetDir = File(wipFolderPath)
        if (!targetDir.exists())
            targetDir.mkdir()

        if (themePath != null)
            unzip(themePath, wipFolderPath)
        else
            unzip(ctx!!, themeUri!!, targetDir)

        val finalJson = mergeJsonThemes(customThemesFolderJson, wipFolderJsonPath)
        File(customThemesFolderJson).writeText(finalJson)

        val wipFolder = File(wipFolderPath)
        for (f in wipFolder.listFiles() ?: return) {
            if (!f.isDirectory)
                continue
            val dir = File(customThemesFolderPath + "/${f.name}")
            dir.mkdir()
            f.copyRecursively(dir, true)
        }

        wipFolder.deleteRecursively()
    }

    fun cleanUp(targetPackage: String) {
        val customThemesFolderPath = "/data/data/$targetPackage/files/custom_themes"
        for (file in File(customThemesFolderPath).listFiles() ?: return) {
            file.deleteRecursively()
        }
    }

    fun openThemesSection(ctx: Context) {
        val i = Intent()
        i.setClassName(
            ctx.packageName,
            "com.touchtype.materialsettings.themessettings.ThemeSettingsActivity"
        )
        ctx.startActivity(i)
    }

    fun retrieveThemes(targetPackage: String): MutableList<KeyboardTheme> {
        val customThemesFolderPath = "/data/data/$targetPackage/files/custom_themes"
        val customThemesFiles = File(customThemesFolderPath)

        val keyboardThemes = ArrayList<KeyboardTheme>()
        for (f in customThemesFiles.listFiles() ?: return mutableListOf()) {
            if (!f.isDirectory) continue
            val keyboardTheme = themeFolderToAppTheme(f) ?: continue
            keyboardThemes.add(keyboardTheme)
        }

        return keyboardThemes
    }

    fun themeFolderToAppTheme(dir: File): KeyboardTheme? {
        val styleJson = dir.absolutePath + "/style.json"
        val styleJsonFile = File(styleJson)
        if (!styleJsonFile.exists()) return null
        val styleJsonText = styleJsonFile.readText()
        val themeNameRgx = Regex("\"name\":\"(.+?(?=\"))")
        val themeName =
            themeNameRgx.find(styleJsonText)?.groupValues?.getOrNull(1)?.trim() ?: return null
        if (themeName == "Theme Customiser") return null

        val thumbnailAbs = dir.absolutePath + "/default/ldpi/thumbnail.png"
        val thumbnailFile = File(thumbnailAbs)
        if (!thumbnailFile.exists()) return KeyboardTheme(themeName, null)

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val bitmap = BitmapFactory.decodeFile(thumbnailFile.absolutePath, options)

        return KeyboardTheme(themeName, dir.name, bitmap)
    }

    fun deleteTheme(targetPackage: String, themeName: String) {
        val customThemesFolderPath = "/data/data/$targetPackage/files/custom_themes"
        val customThemesFolderJson = "$customThemesFolderPath/themelist_custom.json"

        val themesJsonString = filePathToString(customThemesFolderJson)
        val themes: ArrayList<Theme> = ArrayList()
        themes.addAll(
            jsonToThemeObject(themesJsonString).mapNotNull {
                if (it.name == themeName) {
                    val themeFolder = File(customThemesFolderPath + "/${it.id}")
                    if (themeFolder.exists())
                        themeFolder.deleteRecursively()
                    null
                } else {
                    it
                }
            }
        )

        val newThemesJson = Klaxon().toJsonString(Themes(themes.distinctBy { it.id }))
        File(customThemesFolderJson).writeText(newThemesJson)
    }
}
