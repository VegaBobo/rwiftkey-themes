package com.rswiftkey.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rswiftkey.ui.navigation.RwiftkeyNavHost
import com.rswiftkey.ui.theme.SapoTheme

@Composable
fun RwiftkeyApp() {
    SapoTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RwiftkeyNavHost()
        }
    }
}