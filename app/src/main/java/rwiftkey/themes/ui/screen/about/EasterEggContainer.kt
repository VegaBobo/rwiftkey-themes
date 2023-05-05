package rwiftkey.themes.ui.screen.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import rwiftkey.themes.BuildConfig
import rwiftkey.themes.R

@Composable
fun EasterEggContainer(
    isEasterEggVisible: Boolean = false,
    onIncreaseEasterEggAction: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (isEasterEggVisible) {
            AsyncImage(
                model = ContextCompat.getDrawable(LocalContext.current, R.drawable.easter_egg),
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier
                .padding(10.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    onIncreaseEasterEggAction()
                },
            text = "${stringResource(R.string.app_name)} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}