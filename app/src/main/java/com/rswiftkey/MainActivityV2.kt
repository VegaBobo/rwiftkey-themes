package com.rswiftkey

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.rswiftkey.ui.RwiftkeyApp
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        Shell.getShell {}
        setContent { RwiftkeyApp() }
    }
}