package rwiftkey.themes.core

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.beust.klaxon.Klaxon
import rwiftkey.themes.model.Theme
import rwiftkey.themes.model.Themes
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
        val customThemesFile = File(customThemesFolderPath)
        if (!customThemesFile.exists())
            return
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

    fun retrieveThemes(targetPackage: String): List<Theme> {
        val customThemesFolderPath = "/data/data/$targetPackage/files/custom_themes"
        val customThemesFolderJson = "$customThemesFolderPath/themelist_custom.json"

        val themesJsonString = filePathToString(customThemesFolderJson)
        val installedThemes = jsonToThemeObject(themesJsonString)
        for (theme in installedThemes) {
            if(theme.id == installedThemes[0].id) continue
            theme.thumbnail = getThumbnailFromThemeId(targetPackage, theme.id)
        }

        return installedThemes.drop(1)
    }

    fun getThumbnailFromThemeId(targetPackage: String, themeId: String): Bitmap? {
        val thumbnailAbs =
            "/data/data/$targetPackage/files/custom_themes/$themeId/default/xhdpi/thumbnail.png"
        val thumbnailFile = File(thumbnailAbs)

        if (!thumbnailFile.exists()) return null

        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        return BitmapFactory.decodeFile(thumbnailFile.absolutePath, options)
    }

    fun deleteTheme(targetPackage: String, themeId: String) {
        val customThemesFolderPath = "/data/data/$targetPackage/files/custom_themes"
        val customThemesFolderJson = "$customThemesFolderPath/themelist_custom.json"

        val themesJsonString = filePathToString(customThemesFolderJson)
        val themes: ArrayList<Theme> = ArrayList()
        themes.addAll(
            jsonToThemeObject(themesJsonString).mapNotNull {
                if (it.id == themeId) {
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
