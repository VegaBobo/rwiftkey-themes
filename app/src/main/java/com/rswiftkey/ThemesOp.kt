package com.rswiftkey

import android.content.Context
import android.net.Uri
import android.util.Log
import com.beust.klaxon.Klaxon
import com.topjohnwu.superuser.Shell
import com.rswiftkey.sk.Theme
import com.rswiftkey.sk.Themes
import java.io.BufferedReader
import java.io.File

class ThemesOp(
    private val c: Context,
    private val uri: Uri?,
    private val targetPackage: String?
) {

    private val temporaryWorkFolder = c.cacheDir.path + "/theme"
    private val workThemeZipFile = "theme.zip"
    private val absoluteForZipThemeFile = "$temporaryWorkFolder/$workThemeZipFile"
    private val jsonFileFromTheme = "$temporaryWorkFolder/themelist_custom.json"
    private val packageNameFromSKApp = targetPackage
    private val targetJsonWithCurrentThemes = "themelist_custom.json"
    private val skCustomThemesDirectory =
        "/data/data/$packageNameFromSKApp/files/custom_themes"
    private val absoluteForTargetJson =
        "$skCustomThemesDirectory/$targetJsonWithCurrentThemes"
    private val temporarySKJson = "$temporaryWorkFolder/$targetJsonWithCurrentThemes"

    @Throws(Exception::class)
    fun install() {
        println("install call")
        //File(temporaryWorkFolder).deleteRecursively()
        File(temporaryWorkFolder).mkdir()

        Util.copyFile(c, uri!!, absoluteForZipThemeFile)

        Util.unzip(absoluteForZipThemeFile, temporaryWorkFolder)

        val bufferedReader: BufferedReader =
            File(jsonFileFromTheme).bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        val themes: ArrayList<Theme> = ArrayList()

        themes.addAll(Util.jsonToThemeObject(inputString))

        val skInstalledThemes =
            Shell.cmd("cat $absoluteForTargetJson").exec().out[0]

        themes.addAll(Util.jsonToThemeObject(skInstalledThemes))

        val skUid = Shell.cmd("dumpsys package $packageNameFromSKApp | grep -E \"appId=|userId=\"")
            .exec().out[0].trim().split("=")[1]

        val finalJson = Klaxon().toJsonString(Themes(themes.distinctBy { it.id }))

        File(temporarySKJson).writeText(finalJson)

        if(BuildConfig.DEBUG)
            Log.i("JSON", finalJson)

        Shell.cmd("cat $temporarySKJson > $absoluteForTargetJson").exec()
        Shell.cmd("rm $temporarySKJson ; rm $jsonFileFromTheme ; rm $absoluteForZipThemeFile")
            .exec()
        Shell.cmd("cp -frp $temporaryWorkFolder/* $skCustomThemesDirectory").exec()
        Shell.cmd("chown -R $skUid:$skUid $skCustomThemesDirectory").exec()
        Shell.cmd("killall $packageNameFromSKApp").exec()
        File(temporaryWorkFolder).deleteRecursively()
    }

    fun clearThemes() {
        Shell.cmd("killall $packageNameFromSKApp").exec()
        Shell.cmd("rm -rf $skCustomThemesDirectory").exec()
        Shell.cmd("am start $targetPackage/com.touchtype.materialsettings.themessettings.ThemeSettingsActivity")
            .exec()
        Shell.cmd("sleep 2").exec()
        Shell.cmd("killall $packageNameFromSKApp").exec()
        Shell.cmd("sleep 2").exec()
    }

}