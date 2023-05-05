package com.rswiftkey.ui.screen.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.rswiftkey.R
import com.rswiftkey.ui.components.DialogKeyboardSelection
import com.rswiftkey.ui.components.PreferenceItem
import com.rswiftkey.ui.components.RwiftkeyAppBar

@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit,
    settingsVM: SettingsVM = hiltViewModel()
) {
    val uiState by settingsVM.uiState.collectAsState()
    val ctx = LocalContext.current
    val insets = WindowInsets.systemBars.only(WindowInsetsSides.Vertical).asPaddingValues()

    if (uiState.isDialogVisible)
        DialogKeyboardSelection(
            availKeyboards = uiState.availableKeyboards,
            onDismissRequest = { settingsVM.onToggleDialog() },
            onClick = { settingsVM.onClickKeyboardSelection(it) },
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(insets)
    ) {
        RwiftkeyAppBar(
            showSettings = false, title = stringResource(id = R.string.title_activity_preferences)
        )

        PreferenceItem(
            title = stringResource(id = R.string.target_keyboard),
            description = uiState.selectedKeyboard.applicationName,
            icon = ImageVector.vectorResource(id = R.drawable.keyboard),
            onClick = { settingsVM.onToggleDialog() })

        PreferenceItem(
            title = stringResource(id = R.string.clear_themes),
            description = stringResource(id = R.string.clean_installed_themes),
            icon = ImageVector.vectorResource(id = R.drawable.delete),
            onClick = {
                settingsVM.onClickClean(
                    onBeforeClean = {
                        Toast.makeText(
                            ctx,
                            ctx.resources.getString(R.string.please_wait),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onAfterClean = {
                        Toast.makeText(
                            ctx,
                            ctx.resources.getString(R.string.cleaned_installed_themes),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        )

        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = stringResource(id = R.string.about_app),
            icon = ImageVector.vectorResource(id = R.drawable.info),
            onClick = { onAboutClick() }
        )
    }
}
