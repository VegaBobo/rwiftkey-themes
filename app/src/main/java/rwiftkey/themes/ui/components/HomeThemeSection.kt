package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import rwiftkey.themes.R
import rwiftkey.themes.model.Theme

@Composable
fun HomeThemeSection(
    modifier: Modifier = Modifier,
    keyboardThemes: MutableList<Theme> = mutableStateListOf(),
    onClickTheme: (Theme) -> Unit
) {
    if (keyboardThemes.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = stringResource(id = R.string.no_themes),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        LazyVerticalGrid(
            modifier = modifier,
            columns = GridCells.Fixed(2),
        ) {
            items(keyboardThemes.size) {
                val thisKeyboardTheme = keyboardThemes.elementAt(it)
                ThemeCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp),
                    onClick = { onClickTheme(thisKeyboardTheme) },
                    themeName = thisKeyboardTheme.name,
                    thumbnail = thisKeyboardTheme.thumbnail?.asImageBitmap(),
                )
            }
            item(span = { GridItemSpan(2) }) {
                Spacer(
                    modifier = Modifier.padding(64.dp)
                )
            }
        }
    }
}