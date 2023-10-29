package rwiftkey.themes.ui.screen.home

import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import rwiftkey.themes.R
import rwiftkey.themes.core.findActivity
import rwiftkey.themes.model.Theme
import rwiftkey.themes.ui.components.BottomSheetDivisor
import rwiftkey.themes.ui.components.CustomBottomSheet
import rwiftkey.themes.ui.components.RwiftkeyAppBar
import rwiftkey.themes.ui.components.RwiftkeyLoadThemesButton
import rwiftkey.themes.ui.components.RwiftkeyMainFAB
import rwiftkey.themes.ui.components.RwiftkeyPaletteButton
import rwiftkey.themes.ui.components.SimpleListButton
import rwiftkey.themes.ui.components.ThemeThumb
import rwiftkey.themes.ui.util.launchAcResult
import rwiftkey.themes.xposed.IntentAction

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
    val composableScope = rememberCoroutineScope()

    val launcherSelectFile = launchAcResult {
        homeVm.onFileSelected(it.data!!.data!!)
    }

    val barState = rememberTopAppBarState()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(barState)

    val launcherRetrieveThemesXposed = launchAcResult {
        val intent = it.data
        if (intent != null) {
            val keyboardThemes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra("KeyboardThemes", Theme::class.java)
            } else {
                intent.getParcelableArrayListExtra("KeyboardThemes")
            }
        }
    }

    LaunchedEffect(Unit) {
        val data = ctx.findActivity().intent?.data
        if (data != null)
            homeVm.onFileSelected(data)

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

                HomeToast.WORKING -> {
                    Toast.makeText(ctx, ctx.getString(R.string.working), Toast.LENGTH_SHORT)
                        .show()
                }

                else -> {}
            }
            homeVm.setToastState(HomeToast.NONE)
        }
    }

    if (uiState.operationMode == AppOperationMode.NONE) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            ContinueWithXposedContainer {
                composableScope.launch {
                    val i = Intent()
                    i.setClassName(
                        homeVm.sKeyboardManager.getPackage(),
                        "com.touchtype.LauncherActivity"
                    )
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    i.putExtra(IntentAction.BIND, true)
                    launcherRetrieveThemesXposed.launch(i)
                    homeVm.initializeSelfServiceCallbacks()
                }
            }
        }
    } else {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                RwiftkeyAppBar(
                    showSettings = true,
                    onSettingsClick = { onClickSettings() },
                    scrollBehavior = if(uiState.isHomeThemesVisible) scrollBehavior else null,
                    navContent = {
                        AnimatedVisibility(
                            visible = uiState.isHomeThemesVisible,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            IconButton(
                                modifier = Modifier.animateContentSize(),
                                onClick = {
                                    scrollBehavior.state.heightOffset = 0f
                                    homeVm.onClickToggleThemes()
                                }) {
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
                        if (uiState.keyboardThemes.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.no_themes),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
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
                                        themeName = thisKeyboardTheme.name,
                                        thumbnail = thisKeyboardTheme.thumbnail?.asImageBitmap(),
                                    )
                                }
                                item(span = { GridItemSpan(2) }) {
                                    Spacer(
                                        modifier = Modifier.padding(64.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RwiftkeyPaletteButton { homeVm.onClickOpenTheme() }
                            Spacer(modifier = Modifier.padding(4.dp))
                            RwiftkeyLoadThemesButton {
                                homeVm.onClickToggleThemes()
                            }
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
                                        var chooseFile = Intent(Intent.ACTION_OPEN_DOCUMENT)
                                        chooseFile.type = "*/*"
                                        val mimetypes =
                                            arrayOf("application/zip", "application/octet-stream")
                                        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                                        chooseFile = Intent.createChooser(
                                            chooseFile,
                                            ctx.getString(R.string.select_theme)
                                        )
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
                        .heightIn(0.dp, 200.dp)
                        .padding(start = 46.dp)
                ) {
                    LazyColumn {
                        if (!uiState.patchCollection.isEmpty())
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
                        else
                            item { Text(text = "No patches available") }
                    }
                }
            }
            BottomSheetDivisor()
            SimpleListButton(
                icon = Icons.Outlined.Delete,
                text = "Remove theme",
                onClick = { homeVm.onClickDeleteTheme() }
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
            scrollBehavior.state.heightOffset = 0f
            homeVm.onClickToggleThemes()
        }

}
