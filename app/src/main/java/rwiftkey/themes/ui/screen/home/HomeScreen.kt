package rwiftkey.themes.ui.screen.home

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Extension
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import rwiftkey.themes.R
import rwiftkey.themes.core.findActivity
import rwiftkey.themes.ui.components.ContinueWithXposedContainer
import rwiftkey.themes.ui.components.CustomBottomSheet
import rwiftkey.themes.ui.components.HomeAppBar
import rwiftkey.themes.ui.components.HomeScreenBottomFAB
import rwiftkey.themes.ui.components.HomeScreenCenterContainer
import rwiftkey.themes.ui.components.HomeThemeSection
import rwiftkey.themes.ui.components.LoadingOverlay
import rwiftkey.themes.ui.components.SimpleListButton
import rwiftkey.themes.ui.components.ThemeDetailsMenuSheet
import rwiftkey.themes.ui.components.ThemeThumb
import rwiftkey.themes.xposed.IntentAction

@OptIn(ExperimentalMaterial3Api::class)
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

    val barState = rememberTopAppBarState()

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(barState)

    LaunchedEffect(Unit) {
        // TODO handle rootless coming installation
        // or maybe discard it, since we need to bound service first
        val data = ctx.findActivity().intent?.data
        if (data != null)
            homeVm.onFileSelected(data)

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
        AppOperationMode.NONE -> {
            ContinueWithXposedContainer(
                modifier = Modifier.padding(insets),
                onClickContinue = {
                    val remoteAppIntent = Intent()
                    remoteAppIntent.setClassName(
                        homeVm.sKeyboardManager.targetKeyboardPackage,
                        "com.touchtype.LauncherActivity"
                    )
                    remoteAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    remoteAppIntent.putExtra(IntentAction.BIND, true)
                    ctx.startActivity(remoteAppIntent)
                    homeVm.initializeSelfServiceCallbacks()
                }
            )
        }

        AppOperationMode.ROOT, AppOperationMode.XPOSED -> {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    HomeAppBar(
                        onClickSettings = { onClickSettings() },
                        onClickBackButton = { homeVm.onClickShowThemes() },
                        scrollBehavior = if (uiState.isHomeThemesVisible) scrollBehavior else null
                    )
                },
                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        if (uiState.isHomeThemesVisible) {
                            HomeThemeSection(
                                keyboardThemes = uiState.keyboardThemes,
                                onClickTheme = { homeVm.updateSelectedTheme(it) }
                            )
                        } else {
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
            )
        }
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

            if (uiState.isThemeDetailsVisible)
                ThemeDetailsMenuSheet(
                    patchCollection = uiState.patchCollection,
                    onClickApplyPatch = { homeVm.onClickApplyPatch(it) },
                    onClickDeleteTheme = { homeVm.onClickDeleteTheme() }
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
}
