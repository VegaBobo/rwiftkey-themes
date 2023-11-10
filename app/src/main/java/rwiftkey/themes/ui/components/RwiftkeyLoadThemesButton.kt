package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R

@Composable
fun RwiftkeyLoadThemesButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    RwiftkeyButton(
        modifier = modifier,
        innerPadding = 24.dp,
        content = {
            Row {
                Text(text = stringResource(R.string.load_keyboard_themes))
            }
        },
        onClick = { onClick() }
    )
}
