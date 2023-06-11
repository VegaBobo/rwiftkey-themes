package rwiftkey.themes.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import rwiftkey.themes.R

@Composable
fun RwiftkeyAppBar(
    modifier: Modifier = Modifier,
    title: String = stringResource(id = R.string.app_name),
    showSettings: Boolean = false,
    onSettingsClick: () -> Unit = { }
) = LargeTopAppBar(
    modifier = modifier,
    title = { Text(text = title) },
    actions = {
        if (showSettings) {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.settings),
                    contentDescription = "Settings",
                )
            }
        }
    }
)