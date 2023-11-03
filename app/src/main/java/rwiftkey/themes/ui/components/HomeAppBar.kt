package rwiftkey.themes.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    onClickSettings: () -> Unit,
    onClickBackButton: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    RwiftkeyAppBar(
        showSettings = true,
        onSettingsClick = { onClickSettings() },
        scrollBehavior = scrollBehavior,
        navContent = {
            AnimatedVisibility(
                visible = onClickBackButton != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    modifier = Modifier.animateContentSize(),
                    onClick = {
                        scrollBehavior?.state?.heightOffset = 0f
                        onClickBackButton?.invoke()
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            }
        }
    )
}