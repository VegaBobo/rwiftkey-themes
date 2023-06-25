package rwiftkey.themes.core

import android.content.Context
import android.content.Intent
import android.net.Uri
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
}
