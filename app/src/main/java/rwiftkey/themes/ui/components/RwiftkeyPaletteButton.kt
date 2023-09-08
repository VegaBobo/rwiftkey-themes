package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R

@Composable
fun RwiftkeyButton(
    modifier: Modifier = Modifier,
    innerPadding: Dp = 50.dp,
    content: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.onSecondary,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier,
        onClick = onClick
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            content()
        }
    }

}

@Composable
fun RwiftkeyPaletteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    RwiftkeyButton(
        modifier = modifier,
        content = {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.palette),
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Text(text = stringResource(R.string.open_theme_section))
        },
        onClick = { onClick() }
    )
}

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