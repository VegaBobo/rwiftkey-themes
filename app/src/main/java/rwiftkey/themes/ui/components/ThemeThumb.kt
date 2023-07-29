package rwiftkey.themes.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import rwiftkey.themes.R

@Composable
fun ThemeThumb(
    modifier: Modifier = Modifier,
    thumbnail: ImageBitmap? = null,
) {
    if (thumbnail != null)
        Image(
            contentScale = ContentScale.Crop,
            modifier = modifier,
            bitmap = thumbnail,
            contentDescription = ""
        )
    else
        Image(
            contentScale = ContentScale.Crop,
            modifier = modifier,
            painter = painterResource(id = R.drawable.palette),
            contentDescription = null
        )
}