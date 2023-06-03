package rwiftkey.themes.ui.screen.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import rwiftkey.themes.R
import rwiftkey.themes.core.findActivity
import rwiftkey.themes.ui.components.RwiftkeyAppBar
import rwiftkey.themes.ui.components.RwiftkeyMainFAB
import rwiftkey.themes.ui.components.RwiftkeyPaletteButton
import rwiftkey.themes.ui.util.launchAcResult

@Composable
fun HomepageScreen(
    onClickSettings: () -> Unit,
    homeVm: HomeViewModel = hiltViewModel()
) {
    val insets = WindowInsets
        .systemBars
        .only(WindowInsetsSides.Vertical)
        .asPaddingValues()

    val uiState by homeVm.uiState.collectAsState()
    val ctx = LocalContext.current

    var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
    chooseFile.type = "*/*"
    val mimetypes = arrayOf("application/zip", "application/octet-stream")
    chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
    chooseFile = Intent.createChooser(chooseFile, stringResource(R.string.select_theme))

    val launcherSelectFile = launchAcResult {
        homeVm.onFileSelected(it)
    }

    LaunchedEffect(Unit) {
        val givenUri = ctx.findActivity().intent?.data
        if (givenUri != null)
            homeVm.onFileSelected(givenUri)
        snapshotFlow { uiState.homeToast }.collectLatest {
            when (it) {
                HomeToast.INSTALLATION_FAILED ->
                    Toast.makeText(ctx, ctx.getString(R.string.error_theme), Toast.LENGTH_LONG)
                        .show()

                HomeToast.INSTALLATION_FINISHED ->
                    Toast.makeText(ctx, ctx.getString(R.string.theme_installed), Toast.LENGTH_LONG)
                        .show()

                else -> {}
            }
            homeVm.setToastState(HomeToast.NONE)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(insets)
    ) {
        if (uiState.operationMode == AppOperationMode.INCOMPATIBLE) {
            ContinueWithXposedContainer { homeVm.onClickSwitchToXposed() }
        } else {
            RwiftkeyAppBar(
                modifier = Modifier.align(Alignment.TopStart),
                showSettings = true,
                onSettingsClick = { onClickSettings() }
            )

            RwiftkeyPaletteButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = { homeVm.onClickOpenTheme() }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (uiState.isLoadingVisible)
                    CircularProgressIndicator(modifier = Modifier.padding(48.dp))
                else
                    RwiftkeyMainFAB(
                        modifier = Modifier
                            .padding(bottom = 16.dp),
                        onClick = {
                            launcherSelectFile.launch(chooseFile)
                        }
                    )
            }
        }

    }

    if (uiState.hasNoKeyboardsAvail)
        NoKeyboardsAvailDialog(onClickClose = { ctx.findActivity().finishAffinity() })

}