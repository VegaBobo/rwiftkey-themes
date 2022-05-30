package com.rswiftkey

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.rswiftkey.ui.RwiftkeyApp

// TODO: Rename to MainActivity once ready to be released
class MainActivityV2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent { RwiftkeyApp() }
    }
}