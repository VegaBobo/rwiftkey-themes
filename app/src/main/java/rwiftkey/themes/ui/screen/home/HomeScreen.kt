package rwiftkey.themes.ui.screen.home

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import rwiftkey.themes.R
import rwiftkey.themes.core.findActivity
import rwiftkey.themes.ui.components.BottomSheetDivisor
import rwiftkey.themes.ui.components.CustomBottomSheet
import rwiftkey.themes.ui.components.RwiftkeyAppBar
import rwiftkey.themes.ui.components.RwiftkeyLoadThemesButton
import rwiftkey.themes.ui.components.RwiftkeyMainFAB
import rwiftkey.themes.ui.components.RwiftkeyPaletteButton
import rwiftkey.themes.ui.components.SimpleListButton
import rwiftkey.themes.ui.components.ThemeThumb
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

                HomeToast.PATCHED_SUCCESS -> {
                    Toast.makeText(ctx, ctx.getString(R.string.success), Toast.LENGTH_SHORT)
                        .show()
                }

                HomeToast.PATCHED_FAILED -> {
                    Toast.makeText(ctx, ctx.getString(R.string.failed), Toast.LENGTH_SHORT)
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
                    onSettingsClick = { onClickSettings() },
                    navContent = {
                        AnimatedVisibility(visible = uiState.isHomeThemesVisible) {
                            IconButton(
                                modifier = Modifier.animateContentSize(),
                                onClick = { homeVm.onClickToggleThemes() }) {
                                Icon(
                                    imageVector = Icons.Outlined.ArrowBack,
                                    contentDescription = "Back button"
                                )
                            }
                        }
                    }
                )
            },
            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    if (uiState.isHomeThemesVisible) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                        ) {
                            items(uiState.keyboardThemes.size) {
                                val thisKeyboardTheme = uiState.keyboardThemes.elementAt(it)
                                ThemeCard(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
                                    onClick = { homeVm.updateSelectedTheme(thisKeyboardTheme) },
                                    themeName = thisKeyboardTheme.name ?: "No name",
                                    thumbnail = thisKeyboardTheme.thumbnail?.asImageBitmap(),
                                )
                            }
                            item(span = { GridItemSpan(2) }) { Spacer(modifier = Modifier.padding(64.dp)) }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RwiftkeyPaletteButton { homeVm.onClickOpenTheme() }
                            Spacer(modifier = Modifier.padding(4.dp))
                            RwiftkeyLoadThemesButton { homeVm.onClickToggleThemes() }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        ) {
                            if (uiState.isInstallationLoadingVisible)
                                CircularProgressIndicator(modifier = Modifier.padding(48.dp))
                            else
                                RwiftkeyMainFAB(
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    onClick = {
                                        launcherSelectFile.launch(chooseFile)
                                    }
                                )
                        }
                    }
                }
            }
        )
    }

    if (uiState.selectedTheme != null)
        CustomBottomSheet(
            title = uiState.selectedTheme?.name ?: "Theme title",
            onDismiss = { homeVm.updateSelectedTheme(null) }
        ) {
            ThemeThumb(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                thumbnail = uiState.selectedTheme?.thumbnail?.asImageBitmap()
            )
            Spacer(modifier = Modifier.padding(4.dp))
            SimpleListButton(
                icon = Icons.Outlined.Extension,
                text = "Patch theme",
                onClick = { homeVm.onClickPatchTheme() }
            )
            if (uiState.isPatchMenuVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(start = 46.dp)
                ) {
                    LazyColumn() {
                        items(uiState.patchCollection.size) {
                            val thisPatchCollection = uiState.patchCollection[it]
                            Text(
                                text = thisPatchCollection.title,
                                modifier = Modifier.fillMaxWidth()
                            )
                            BottomSheetDivisor()
                            for (item in thisPatchCollection.patches) {
                                Column(modifier = Modifier.clickable {
                                    homeVm.onClickApplyPatch(item)
                                }) {
                                    Text(text = item.title, modifier = Modifier.fillMaxWidth())
                                    AsyncImage(
                                        model = item.thumbnail,
                                        contentDescription = "thumbnail"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }
            BottomSheetDivisor()
            SimpleListButton(
                icon = Icons.Outlined.Delete,
                text = "Remove theme",
                onClick = { homeVm.onClickDeleteThemeRoot() }
            )
            Spacer(modifier = Modifier.padding(4.dp))
        }

    if (uiState.hasNoKeyboardsAvail)
        NoKeyboardsAvailDialog(onClickClose = { ctx.findActivity().finishAffinity() })

    if (uiState.isLoadingOverlayVisible) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f)
        ) {}
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .alpha(1f)
                    .size(52.dp)
            )
        }
        BackHandler { }
    }

    if (uiState.isHomeThemesVisible)
        BackHandler {
            homeVm.onClickToggleThemes()
        }

}