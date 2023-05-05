package com.rswiftkey.ui.screen.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.rswiftkey.BuildConfig
import com.rswiftkey.R
import com.rswiftkey.ui.components.PreferenceItem
import com.rswiftkey.ui.components.PreferenceTitle

@Composable
fun AboutScreen(
    aboutVM: AboutVM = viewModel()
) {
    val uriHandler = LocalUriHandler.current
    val uiState by aboutVM.uiState.collectAsState()
    val insets = WindowInsets.systemBars.only(WindowInsetsSides.Vertical).asPaddingValues()

    Scaffold(
        modifier = Modifier.padding(insets),
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
                //LibrariesDialog()
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
                ) { aboutVM.toggleLibrariesDialog() }
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (uiState.isEasterEggVisible) {
                    AsyncImage(
                        model = getDrawable(LocalContext.current, R.drawable.easter_egg),
                        contentDescription = null
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            if (aboutVM.easterEggCounter <= 8)
                                aboutVM.increaseEasterEggCounter()
                        },
                    text = "${stringResource(R.string.app_name)} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}