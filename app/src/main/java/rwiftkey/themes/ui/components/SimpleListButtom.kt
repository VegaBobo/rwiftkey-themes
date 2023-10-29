package rwiftkey.themes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SimpleListButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(modifier = modifier
        .clickable { onClick() }
        .padding(6.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = "Icon")
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = text, fontSize = 18.sp)
    }
}
