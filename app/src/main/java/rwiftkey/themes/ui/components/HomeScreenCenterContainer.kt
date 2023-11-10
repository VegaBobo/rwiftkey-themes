package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenCenterContainer(
    onClickOpenThemes: () -> Unit,
    onClickShowThemes: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RwiftkeyPaletteButton { onClickOpenThemes() }
        Spacer(modifier = Modifier.padding(4.dp))
        RwiftkeyLoadThemesButton { onClickShowThemes() }
    }
}