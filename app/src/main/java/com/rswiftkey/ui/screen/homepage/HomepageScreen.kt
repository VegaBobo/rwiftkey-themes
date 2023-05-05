package com.rswiftkey.ui.screen.homepage

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rswiftkey.MainActivityV2
import com.rswiftkey.ui.components.RwiftkeyAppBar
import com.rswiftkey.ui.components.RwiftkeyMainFAB
import com.rswiftkey.ui.components.RwiftkeyPaletteButton

@Composable
fun HomepageScreen(
    modifier: Modifier = Modifier,
    onClickSettings: () -> Unit,
    homeVm: HomepageVM = hiltViewModel()
) {
    val insets = WindowInsets
        .systemBars
        .only(WindowInsetsSides.Vertical)
        .asPaddingValues()

    val activity = LocalContext.current as MainActivityV2

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(insets)
    ) {

        RwiftkeyAppBar(
            modifier = Modifier.align(Alignment.TopStart),
            showSettings = true,
            onSettingsClick = { onClickSettings() }
        )

        RwiftkeyPaletteButton(
            modifier = Modifier.align(Alignment.Center),
            onClick = { homeVm.openThemeSection() }
        )

        RwiftkeyMainFAB(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            onClick = {
                activity.launch(activity.fileSelection!!)
            }
        )
    }

}