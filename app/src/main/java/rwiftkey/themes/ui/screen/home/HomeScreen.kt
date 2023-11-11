package rwiftkey.themes.ui.screen.home

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import rwiftkey.themes.R
import rwiftkey.themes.core.findActivity
import rwiftkey.themes.ui.components.ContinueWithXposedContainer
import rwiftkey.themes.ui.components.HomeAppBar
import rwiftkey.themes.ui.components.HomeScreenBottomFAB
import rwiftkey.themes.ui.components.HomeScreenCenterContainer
import rwiftkey.themes.ui.components.HomeThemeSection
import rwiftkey.themes.ui.components.LoadingOverlay
import rwiftkey.themes.ui.components.NoKeyboardsAvailDialog
import rwiftkey.themes.ui.components.SelectedThemeBottomSheet
import rwiftkey.themes.xposed.IntentAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomepageScreen(
    onClickSettings: () -> Unit,
    homeVm: HomeViewModel = hiltViewModel()
) {
    val uiState by homeVm.uiState.collectAsState()
    val ctx = LocalContext.current

    val barState = rememberTopAppBarState()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(barState)

    LaunchedEffect(Unit) {
        val intent = ctx.findActivity().intent
        val data = intent?.data
        if (data != null)
            homeVm.onFileSelected(data) { intent.data = null }

        snapshotFlow { uiState.homeToast }.collectLatest {
            when (it) {
                HomeToast.INSTALLATION_FAILED -> {
                    Toast.makeText(ctx, ctx.getString(R.string.error_theme), Toast.LENGTH_LONG)
                        .show()
                }

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

    when (uiState.operationMode) {
        OperationMode.NONE -> {
            ContinueWithXposedContainer(
                onClickContinue = {
                    homeVm.initializeSelfServiceCallbacks(
                        onReady = {
                            val remoteAppIntent = Intent()
                            remoteAppIntent.setClassName(
                                homeVm.session.targetKeyboardPackage,
                                "com.touchtype.LauncherActivity"
                            )
                            remoteAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            remoteAppIntent.putExtra(IntentAction.BIND, true)
                            ctx.startActivity(remoteAppIntent)
                        }
                    )
                }
            )
        }

        OperationMode.ROOT, OperationMode.XPOSED -> {
            Scaffold(
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    HomeAppBar(
                        isBackButtonVisible = uiState.isHomeThemesVisible,
                        scrollBehavior = if (uiState.isHomeThemesVisible) scrollBehavior else null,
                        onClickSettings = { onClickSettings() },
                        onClickBackButton = { homeVm.onClickShowThemes() }
                    )
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (uiState.isHomeThemesVisible) {
                            HomeThemeSection(
                                modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                                keyboardThemes = uiState.keyboardThemes,
                                onClickTheme = { homeVm.updateSelectedTheme(it) }
                            )
                        } else {
                            Box(modifier = Modifier.padding(paddingValues)) {
                                HomeScreenCenterContainer(
                                    onClickOpenThemes = { homeVm.onClickOpenThemesSection() },
                                    onClickShowThemes = { homeVm.onClickShowThemes() }
                                )
                                HomeScreenBottomFAB(
                                    modifier = Modifier.align(Alignment.BottomCenter),
                                    onFileSelected = { homeVm.onFileSelected(it) }
                                )
                            }
                        }
                    }
                }
            )
        }

    }

    if (uiState.selectedTheme != null)
        SelectedThemeBottomSheet(
            title = uiState.selectedTheme?.name ?: stringResource(R.string.untitled),
            thumbnail = uiState.selectedTheme?.thumbnail?.asImageBitmap(),
            isPatchMenuVisible = uiState.isPatchMenuVisible,
            patchCollection = uiState.patchCollection,
            onClickLoadPatches = { homeVm.onClickLoadPatches() },
            onClickApplyPatch = { homeVm.onClickApplyPatch(it) },
            onClickDeleteTheme = { homeVm.onClickDeleteTheme() },
            onDismiss = { homeVm.updateSelectedTheme(null) },
        )

    if (uiState.hasNoKeyboardsAvail)
        NoKeyboardsAvailDialog(
            onClickClose = { ctx.findActivity().finishAffinity() }
        )

    if (uiState.isLoadingOverlayVisible)
        LoadingOverlay()

    if (uiState.isHomeThemesVisible)
        BackHandler {
            scrollBehavior.state.heightOffset = 0f
            homeVm.onClickShowThemes()
        }

}
