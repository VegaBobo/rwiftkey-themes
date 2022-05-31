package com.rswiftkey.ui.homepage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rswiftkey.ui.components.RwiftkeyAppBar
import com.rswiftkey.ui.components.RwiftkeyMainFAB
import com.rswiftkey.ui.components.RwiftkeyPaletteButton

@Composable
fun HomepageScreen(
    modifier: Modifier = Modifier
) {
    val insets = WindowInsets
        .systemBars
        .only(WindowInsetsSides.Vertical)
        .asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(insets)
    ) {

        RwiftkeyAppBar(modifier = Modifier.align(Alignment.TopStart), showSettings = true)

        RwiftkeyPaletteButton(
            modifier = Modifier.align(Alignment.Center)
        ) {

        }

        RwiftkeyMainFAB(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}