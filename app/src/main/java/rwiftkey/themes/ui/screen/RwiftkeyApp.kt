package rwiftkey.themes.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import rwiftkey.themes.ui.theme.RwiftKeyTheme

@Composable
fun RwiftkeyApp() {
    RwiftKeyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            RwiftkeyNavHost()
        }
    }
}