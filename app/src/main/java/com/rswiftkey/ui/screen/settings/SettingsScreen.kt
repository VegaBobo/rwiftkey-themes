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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val ctx = LocalContext.current
    val insets = WindowInsets.systemBars.only(WindowInsetsSides.Vertical).asPaddingValues()

    LaunchedEffect(Unit) {
        snapshotFlow { uiState.settingToast }.collectLatest {
            when (it) {
                SettingToast.PLEASE_WAIT ->
                    Toast.makeText(ctx, ctx.getString(R.string.error_theme), Toast.LENGTH_LONG)
                        .show()

                SettingToast.THEMES_CLEANED ->
                    Toast.makeText(ctx, ctx.getString(R.string.theme_installed), Toast.LENGTH_LONG)
                        .show()

                else -> {}
            }
            settingsViewModel.setToastState(SettingToast.NONE)
        }
    }

    if (uiState.isDialogVisible)
        DialogKeyboardSelection(
            availKeyboards = uiState.availableKeyboards,
            onDismissRequest = { settingsViewModel.onToggleDialog() },
            onClick = { settingsViewModel.onClickKeyboardSelection(it) },
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
            onClick = { settingsViewModel.onToggleDialog() })

        PreferenceItem(
            title = stringResource(id = R.string.clear_themes),
            description = stringResource(id = R.string.clean_installed_themes),
            icon = ImageVector.vectorResource(id = R.drawable.delete),
            onClick = { settingsViewModel.onClickClean() }
        )

        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = stringResource(id = R.string.about_app),
            icon = ImageVector.vectorResource(id = R.drawable.info),
            onClick = { onAboutClick() }
        )
    }
}
