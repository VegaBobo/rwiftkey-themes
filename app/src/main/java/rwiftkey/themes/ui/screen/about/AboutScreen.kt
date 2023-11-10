package rwiftkey.themes.ui.screen.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import rwiftkey.themes.R
import rwiftkey.themes.ui.components.EasterEggContainer
import rwiftkey.themes.ui.components.LibrariesDialog
import rwiftkey.themes.ui.components.PreferenceItem
import rwiftkey.themes.ui.components.PreferenceTitle

@Composable
fun AboutScreen(
    aboutViewModel: AboutViewModel = viewModel()
) {
    val uriHandler = LocalUriHandler.current
    val uiState by aboutViewModel.uiState.collectAsState()

    @OptIn(ExperimentalMaterial3Api::class)
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(R.string.about))
                }
            )
        },
        content = { innerPadding ->
            Column(
                Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                PreferenceTitle(title = stringResource(R.string.application))
                PreferenceItem(
                    title = stringResource(R.string.repository),
                    description = stringResource(R.string.view_source),
                    icon = ImageVector.vectorResource(id = R.drawable.code)
                ) { uriHandler.openUri("https://github.com/VegaBobo/rwiftkey-themes") }
                PreferenceItem(
                    title = stringResource(R.string.libraries),
                    description = stringResource(R.string.used_libs),
                    icon = ImageVector.vectorResource(id = R.drawable.code)
                ) { aboutViewModel.toggleLibrariesDialog() }
                PreferenceTitle(title = stringResource(R.string.authors))
                PreferenceItem(
                    title = "RKBDI",
                    description = stringResource(R.string.view_on_twitter),
                    icon = ImageVector.vectorResource(id = R.drawable.accountcircle)
                )
                { uriHandler.openUri("https://twitter.com/RKBDI") }
                PreferenceItem(
                    title = "VegaBobo",
                    description = stringResource(R.string.view_on_github),
                    icon = ImageVector.vectorResource(id = R.drawable.accountcircle)
                )
                { uriHandler.openUri("https://github.com/VegaBobo") }
            }
        },
        bottomBar = {
            EasterEggContainer(
                isEasterEggVisible = uiState.isEasterEggVisible,
                onIncreaseEasterEggAction = { aboutViewModel.increaseEasterEggCounter() }
            )
        }
    )

    if (uiState.isLibrariesDialogVisible) {
        LibrariesDialog(onToggleDialog = { aboutViewModel.toggleLibrariesDialog() })
    }
}