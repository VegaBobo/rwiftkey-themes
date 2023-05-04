package com.rswiftkey.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.rswiftkey.R
import com.rswiftkey.Util
import com.rswiftkey.ui.components.DialogKeyboardSelection
import com.rswiftkey.ui.components.PreferenceItem
import com.rswiftkey.ui.components.RwiftkeyAppBar
import com.rswiftkey.util.KeyboardUtils
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit
) {

    val insets = WindowInsets.systemBars.only(WindowInsetsSides.Vertical).asPaddingValues()

    var showDialog by remember { mutableStateOf(false) }
    var currentKeyboard by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val c = LocalContext.current

    val sk = KeyboardUtils.obtainSKeyboard(c)

    if (showDialog) DialogKeyboardSelection(
        availKeyboards = sk.keyboards,
        onDismissRequest = { showDialog = !showDialog },
        onClick = {
            scope.launch {
                sk.setTargetKeyboard(c, it)
                currentKeyboard = it.applicationName
            }
            showDialog = !showDialog
        },
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(insets)
    ) {

        RwiftkeyAppBar(
            showSettings = false, title = stringResource(id = R.string.title_activity_preferences)
        )

        LaunchedEffect(key1 = Unit) {
            currentKeyboard = sk.getName(c)
        }

        PreferenceItem(title = stringResource(id = R.string.target_keyboard),
            description = currentKeyboard,
            icon = ImageVector.vectorResource(id = R.drawable.keyboard),
            onClick = { showDialog = !showDialog })

        PreferenceItem(
            title = stringResource(id = R.string.clear_themes),
            description = stringResource(id = R.string.clean_installed_themes),
            icon = ImageVector.vectorResource(id = R.drawable.delete),
            onClick = {
                //Util.deleteThemes(sk, scope, c)
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
