package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R

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
                contentDescription = "Palette icon",
                modifier = Modifier.size(96.dp)
            )
            Text(text = stringResource(R.string.open_theme_section))
        },
        onClick = { onClick() }
    )
}
