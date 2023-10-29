package rwiftkey.themes.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import rwiftkey.themes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RwiftkeyAppBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.app_name),
    showSettings: Boolean = false,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navContent: @Composable () -> Unit = {},
    onSettingsClick: () -> Unit = { }
) = LargeTopAppBar(
    modifier = modifier,
    navigationIcon = navContent,
    scrollBehavior = scrollBehavior,
    title = { Text(text = title) },
    actions = {
        if (showSettings) {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.settings),
                    contentDescription = "Settings"
                )
            }
        }
    }
)
