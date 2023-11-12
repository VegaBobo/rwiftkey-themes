package rwiftkey.themes.ui.screen.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
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
import kotlinx.coroutines.flow.collectLatest
import rwiftkey.themes.R
import rwiftkey.themes.ui.components.DialogKeyboardSelection
import rwiftkey.themes.ui.components.PreferenceItem
import rwiftkey.themes.ui.components.RwiftkeyAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit,
    navigateUp: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val ctx = LocalContext.current

    LaunchedEffect(Unit) {
        snapshotFlow { uiState.settingToast }.collectLatest {
            when (it) {
                SettingToast.PLEASE_WAIT ->
                    Toast.makeText(ctx, ctx.getString(R.string.please_wait), Toast.LENGTH_SHORT)
                        .show()

                SettingToast.THEMES_CLEANED ->
                    Toast.makeText(ctx, ctx.getString(R.string.clean_success), Toast.LENGTH_SHORT)
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
            onClick = {
                settingsViewModel.onClickKeyboardSelection(
                    target = it,
                    onChangeXposedTarget = { navigateUp() }
                )
            },
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
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
