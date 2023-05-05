package com.rswiftkey.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
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
import com.rswiftkey.vm.SettingsVM
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit,
    settingsVM: SettingsVM = hiltViewModel()
) {
    val uiState by settingsVM.uiState.collectAsState()
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
            onClick = { settingsVM.onClickClean() }
        )

        PreferenceItem(
            title = stringResource(id = R.string.about),
            description = stringResource(id = R.string.about_app),
            icon = ImageVector.vectorResource(id = R.drawable.info),
            onClick = { onAboutClick() }
        )
    }
}
