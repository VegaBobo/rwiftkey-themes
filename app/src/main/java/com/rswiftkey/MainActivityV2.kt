package com.rswiftkey

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.rswiftkey.ui.RwiftkeyApp
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename to MainActivity once ready to be released
@AndroidEntryPoint
class MainActivityV2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent { RwiftkeyApp() }
    }
}