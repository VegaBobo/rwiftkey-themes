package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetDivisor() {
    Divider(
        modifier = Modifier.padding(4.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        thickness = 1.dp
    )
}