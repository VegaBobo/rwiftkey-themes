package rwiftkey.themes.core

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.beust.klaxon.Klaxon
import org.json.JSONObject
import rwiftkey.themes.model.Theme
import rwiftkey.themes.model.Themes
import java.io.File
import java.nio.charset.Charset

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

    fun modifyTheme(
        targetPackage: String,
        themeId: String,
        zipFileUri: Uri? = null,
        zipFile: File? = null,
        ctx: Context? = null,
    ) {
        val workingThemeDir =
            File("/data/data/$targetPackage/files/custom_themes/$themeId")

        if (zipFileUri != null && ctx != null)
            unzip(ctx, zipFileUri, workingThemeDir)
        else
            unzip(zipFile!!, workingThemeDir)

        val styleJson =
            File("/data/data/$targetPackage/files/custom_themes/$themeId/style.json")

        val formattedJson = JSONObject(styleJson.readText()).toString(2).replace("\\", "")

        var newContents = ""
        var lastLine = ""
        formattedJson.lines().forEach { line ->
            newContents += if (line.contains("sha1") && lastLine.contains("path")) {
                val extractedPath = lastLine.split("\"")[3]
                val filePathFromPath =
                    "/data/data/$targetPackage/files/custom_themes/$themeId/$extractedPath"
                val fileFromPath = File(filePathFromPath)
                val newSha1 = fileFromPath.calculateSHA1()

                val oldSha1 = line.split("\"")[3]
                val newLineWithNewSha1 = line.replace(oldSha1, newSha1)
                newLineWithNewSha1 + "\n"
            } else {
                line + "\n"
            }
            lastLine = line
        }
        styleJson.writeText(newContents, Charset.defaultCharset())
    }

    fun modifyThemeRootless(targetPackage: String, themeId: String, zipFileUri: Uri, ctx: Context) {
        modifyTheme(targetPackage, themeId, zipFileUri, null, ctx)
    }

    fun modifyThemeRoot(targetPackage: String, themeId: String, zipFile: File) {
        modifyTheme(targetPackage, themeId, null, zipFile, null)
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
            if (theme.id == installedThemes[0].id) continue
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
