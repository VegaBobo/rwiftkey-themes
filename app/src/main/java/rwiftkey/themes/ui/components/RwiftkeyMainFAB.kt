package rwiftkey.themes.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RwiftkeyMainFAB(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) = LargeFloatingActionButton(
    modifier = modifier,
    onClick = onClick,
    shape = CircleShape,
    elevation = FloatingActionButtonDefaults.loweredElevation()
) {
    Icon(
        Icons.Rounded.Add,
        contentDescription = "Add",
        modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
    )
}