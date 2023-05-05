package com.rswiftkey

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.rswiftkey.ui.RwiftkeyApp
import com.rswiftkey.vm.HomepageVM
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// TODO: Rename to MainActivity once ready to be released
@AndroidEntryPoint
class MainActivityV2 : AppCompatActivity() {

    companion object {
        init {
            // Set settings before the main shell can be created
            //Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_MOUNT_MASTER)
                    .setTimeout(10)
            )
        }
    }

    private val mainActivityVM: HomepageVM by viewModels()

    var fileSelection: ActivityResultLauncher<Intent>? = null

    @Inject
    lateinit var sKeyboardManager: SKeyboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        Shell.getShell {}

        setContent { RwiftkeyApp() }

        fileSelection =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val uri = result.data!!.data
                    mainActivityVM.addTheme(uri!!, sKeyboardManager)
                }
            }

    }

    fun launch(arl: ActivityResultLauncher<Intent>) {
        var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
        chooseFile.type = "*/*"
        val mimetypes = arrayOf("application/zip", "application/octet-stream")
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
        chooseFile = Intent.createChooser(chooseFile, getString(R.string.select_theme))
        arl.launch(chooseFile)
    }
}