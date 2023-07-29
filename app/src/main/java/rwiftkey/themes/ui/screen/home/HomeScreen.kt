package rwiftkey.themes.ui.screen.home

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import rwiftkey.themes.R
import rwiftkey.themes.core.findActivity
import rwiftkey.themes.ui.components.CustomBottomSheet
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

        if (uiState.operationMode == AppOperationMode.ROOT)
            homeVm.loadThemesRoot()

        snapshotFlow { uiState.homeToast }.collectLatest {
            when (it) {
                HomeToast.INSTALLATION_FAILED ->
                    Toast.makeText(ctx, ctx.getString(R.string.error_theme), Toast.LENGTH_LONG)
                        .show()

                HomeToast.INSTALLATION_FINISHED -> {
                    Toast.makeText(ctx, ctx.getString(R.string.theme_installed), Toast.LENGTH_LONG)
                        .show()
                }

                else -> {}
            }
            homeVm.setToastState(HomeToast.NONE)
        }
    }

    if (uiState.operationMode == AppOperationMode.INCOMPATIBLE) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            ContinueWithXposedContainer { homeVm.onClickSwitchToXposed() }
        }
    } else {
        Scaffold(
            topBar = {
                RwiftkeyAppBar(
                    showSettings = true,
                    onSettingsClick = { onClickSettings() }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                    ) {
                        item(span = { GridItemSpan(2) }) {
                            RwiftkeyPaletteButton(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .align(Alignment.Center),
                                onClick = { homeVm.onClickOpenTheme() }
                            )
                        }
                        items(uiState.keyboardThemes.size) {
                            val thisKeyboardTheme = uiState.keyboardThemes.elementAt(it)
                            ThemeCard(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                                onClick = { homeVm.toggleSheet() },
                                themeName = thisKeyboardTheme.name ?: "No name",
                                thumbnail = thisKeyboardTheme.thumbnail?.asImageBitmap(),
                            )
                        }
                        item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.padding(64.dp)) }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
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
        )
    }

    if (uiState.isBottomSheetVisible)
        CustomBottomSheet(
            title = "Test",
            onDismiss = { homeVm.toggleSheet() }
        ) {

        }

    if (uiState.hasNoKeyboardsAvail)
        NoKeyboardsAvailDialog(onClickClose = { ctx.findActivity().finishAffinity() })

}