package com.rswiftkey.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.rswiftkey.R
import com.rswiftkey.ui.components.PreferenceItem
import com.rswiftkey.ui.components.RwiftkeyAppBar

@Composable
fun SettingsScreen() {

    val insets = WindowInsets
        .systemBars
        .only(WindowInsetsSides.Vertical)
        .asPaddingValues()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(insets)
    ){

        RwiftkeyAppBar(
            showSettings = false,
            title = stringResource(id = R.string.title_activity_preferences)
        )

        PreferenceItem(
            title = stringResource(id = R.string.target_keyboard),
            description = "placeholder",
            icon = ImageVector.vectorResource(id = R.drawable.keyboard)
        ) {

        }
        PreferenceItem(
            title = stringResource(id = R.string.clear_themes),
            description = "placeholder",
            icon = ImageVector.vectorResource(id = R.drawable.delete)
        ) {

        }
        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = "placeholder",
            icon = ImageVector.vectorResource(id = R.drawable.info)
        ) {

        }
    }

}
